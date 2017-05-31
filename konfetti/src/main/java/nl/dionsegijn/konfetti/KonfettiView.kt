package nl.dionsegijn.konfetti

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

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

    val systems: MutableList<ParticleSystem> = mutableListOf()
    var timer: TimerIntegration = TimerIntegration()

    fun build(): ParticleSystem {
        return ParticleSystem(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val deltaTime = timer.getDeltaTime()
        for (i in systems.size - 1 downTo 0) {
            val konfetti = systems[i]
            konfetti.emitter.render(canvas, deltaTime)
            if (konfetti.doneEmitting()) systems.removeAt(i)
        }

        if (systems.size != 0) {
            invalidate()
        } else {
            timer.reset()
        }
    }

    fun start(particleSystem: ParticleSystem) {
        systems.add(particleSystem)
        invalidate()
    }

    /**
     * TimerIntegration retrieves the delta time since the rendering of the previous frame.
     * Delta time is used to draw the confetti correctly if any frame drops occur.
     */
    class TimerIntegration {
        var previousTime: Long = -1L

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
