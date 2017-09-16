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
class KonfettiView : View {

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
    private var onParticleSystemUpdateListener: OnParticleSystemUpdateListener? = null

    fun getActiveSystems() = systems

    fun build(): ParticleSystem = ParticleSystem(this)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val deltaTime = timer.getDeltaTime()
        for (i in systems.size - 1 downTo 0) {
            val particleSystem = systems[i]
            particleSystem.renderSystem.render(canvas, deltaTime)
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
     * Abruptly stop all particle systems from rendering
     * The canvas will stop drawing all particles. Everything that's being rendered will directly
     * disappear from the view.
     */
    fun reset() {
        systems.clear()
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

            if(previousTime == -1L) previousTime = System.nanoTime()

            val currentTime = System.nanoTime()
            val dt: Long = (currentTime - previousTime) / 1000000
            previousTime = currentTime
            return dt.toFloat() / 1000
        }
    }
}
