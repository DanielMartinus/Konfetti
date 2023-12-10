package nl.dionsegijn.konfetti.xml

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.xml.listeners.OnParticleSystemUpdateListener

/**
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
    private val systems: MutableList<PartySystem> = mutableListOf()

    /**
     * Keeping track of the delta time between frame rendering
     */
    private var timer: TimerIntegration = TimerIntegration()

    private var drawArea = Rect()

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val deltaTime = timer.getDeltaTime()
        for (i in systems.size - 1 downTo 0) {
            val partySystem = systems[i]

            val totalTimeRunning = timer.getTotalTimeRunning(partySystem.createdAt)
            if (totalTimeRunning >= partySystem.party.delay) {
                partySystem.render(deltaTime, drawArea).forEach {
                    it.display(canvas)
                }
            }

            if (partySystem.isDoneEmitting()) {
                systems.removeAt(i)
                onParticleSystemUpdateListener?.onParticleSystemEnded(this, partySystem.party, systems.size)
            }
        }

        if (systems.size != 0) {
            invalidate()
        } else {
            timer.reset()
        }
    }

    private val paint: Paint = Paint()

    private fun Particle.display(canvas: Canvas) {
        // setting alpha via paint.setAlpha allocates a temporary "ColorSpace$Named" object
        // it is more efficient via setColor
        paint.color = color

        val centerX = scaleX * width / 2

        val saveCount = canvas.save()
        canvas.translate(x - centerX, y)
        canvas.rotate(rotation, centerX, width / 2)
        canvas.scale(scaleX, 1f)

        shape.draw(canvas, paint, width)
        canvas.restoreToCount(saveCount)
    }

    fun start(vararg party: Party) {
        systems.addAll(
            party.map {
                onParticleSystemUpdateListener?.onParticleSystemStarted(this, it, systems.size)
                PartySystem(it)
            }
        )
        invalidate()
    }

    fun start(party: List<Party>) {
        systems.addAll(
            party.map {
                onParticleSystemUpdateListener?.onParticleSystemStarted(this, it, systems.size)
                PartySystem(it)
            }
        )
        invalidate()
    }

    fun start(party: Party) {
        systems.add(PartySystem(party))
        onParticleSystemUpdateListener?.onParticleSystemStarted(this, party, systems.size)
        invalidate()
    }

    /**
     * Stop a particular particle system. All particles belonging to this system will directly disappear from the view.
     */
    fun stop(party: Party) {
        systems.removeAll { it.party == party }
        onParticleSystemUpdateListener?.onParticleSystemEnded(this, party, systems.size)
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
        systems.forEach { it.enabled = false }
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawArea = Rect(0, 0, w, h)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        timer.reset()
    }
}
