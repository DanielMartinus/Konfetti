package nl.dionsegijn.konfetti

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import nl.dionsegijn.konfetti.listeners.OnParticleSystemUpdateListener

/**
 * Created by dionsegijn on 3/25/17.
 * Implement this view to render the particles on.
 * Call [build] to setup a particle system. KonfettiView will then invalidate
 * pass the canvas to each system where each system will handle the rendering.
 */
open class KonfettiView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Active particle systems
     */
    private val systems: MutableList<ParticleSystem> = mutableListOf()

    /**
     * Keeping track of the delta time between frame rendering
     */
    private var timer: TimerIntegration = TimerIntegration()

    /**
     * [OnParticleSystemUpdateListener] listener to notify when a new particle system
     * starts rendering and when a particle system stopped rendering
     */
    var onParticleSystemUpdateListener: OnParticleSystemUpdateListener? = null

    fun getActiveSystems() = systems

    /**
     * Check if current systems are active rendering particles.
     * @return true if konfetti is actively rendering
     *         false if everything stopped rendering
     */
    fun isActive() = systems.isNotEmpty()

    fun build(): ParticleSystem = ParticleSystem(this)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val deltaTime = timer.getDeltaTime()
        for (i in systems.size - 1 downTo 0) {
            val particleSystem = systems[i]

            val totalTimeRunning = timer.getTotalTimeRunning(particleSystem.renderSystem.createdAt)
            if (totalTimeRunning >= particleSystem.getDelay()) {
                particleSystem.renderSystem.render(canvas, deltaTime)
            }

            if (particleSystem.doneEmitting()) {
                systems.removeAt(i)
                onParticleSystemUpdateListener?.onParticleSystemEnded(this, particleSystem, systems.size)
            }
        }

        if (systems.size != 0) {
            invalidate()
        } else {
            timer.reset()
        }
    }

    fun start(particleSystem: ParticleSystem) {
        systems.add(particleSystem)
        onParticleSystemUpdateListener?.onParticleSystemStarted(this, particleSystem, systems.size)
        invalidate()
    }

    /**
     * Stop a particular particle system. All particles belonging to this system will directly disappear from the view.
     */
    fun stop(particleSystem: ParticleSystem) {
        systems.remove(particleSystem)
        onParticleSystemUpdateListener?.onParticleSystemEnded(this, particleSystem, systems.size)
    }

    /**
     * Abruptly stop all particle systems from rendering
     * The canvas will stop drawing all particles. Everything that's being rendered will directly
     * disappear from the view.
     */
    fun reset() {
        systems.clear()
    }

    /**
     * Stop the particle systems from rendering new particles. All particles already visible
     * will continue rendering until they're done. When all particles are done rendering the system
     * will be removed.
     */
    fun stopGracefully() {
        systems.forEach { it.renderSystem.enabled = false }
    }

    /**
     * TimerIntegration retrieves the delta time since the rendering of the previous frame.
     * Delta time is used to draw the confetti correctly if any frame drops occur.
     */
    class TimerIntegration {
        private var previousTime: Long = -1L

        fun reset() {
            previousTime = -1L
        }

        fun getDeltaTime(): Float {
            if (previousTime == -1L) previousTime = System.nanoTime()

            val currentTime = System.nanoTime()
            val dt = (currentTime - previousTime) / 1000000f
            previousTime = currentTime
            return dt / 1000
        }

        fun getTotalTimeRunning(startTime: Long): Long {
            val currentTime = System.currentTimeMillis()
            return (currentTime - startTime)
        }
    }
}
