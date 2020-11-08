package nl.dionsegijn

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.ParticleSystem
import nl.dionsegijn.konfetti.core.Configuration
import nl.dionsegijn.konfetti.core.Timeline
import nl.dionsegijn.konfetti.emitters.PartyEmitter
import nl.dionsegijn.konfetti.listeners.OnParticleSystemUpdateListener
import java.sql.Time

open class PartyView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var timeline: Timeline? = null
    /**
     * Active particle systems
     */
    private val emitters: MutableList<PartyEmitter> = mutableListOf()

    fun start(configuration: Configuration) = start(Timeline().add(configuration))

    fun start(timeline: Timeline) {
        this.timeline = timeline
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val deltaTime = timer.getDeltaTime()


    }

    /**
     * Keeping track of the delta time between frame rendering
     */
    private val timer: TimerIntegration = TimerIntegration()

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
            val dt: Long = (currentTime - previousTime) / 1000000
            previousTime = currentTime
            return dt.toFloat() / 1000
        }

        fun getTotalTimeRunning(startTime: Long): Long {
            val currentTime = System.currentTimeMillis()
            return (currentTime - startTime)
        }
    }

}
