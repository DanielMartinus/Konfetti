package nl.dionsegijn.konfetti

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View




/**
 * Created by dionsegijn on 3/25/17.
 */
class KonfettiView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val systems: MutableList<ParticleSystem> = mutableListOf()

    fun build(): ParticleSystem {
        return ParticleSystem(this)
    }

    val paint: Paint = Paint()

    init {
        paint.color = Color.BLACK
    }

    // TODO: Remove particle system when it's done (no particles are left over anymore and/or time is up)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        calcFps()

        val it = systems.iterator()
        while (it.hasNext()) {
            val konfetti = it.next()
            konfetti.emitter.render(canvas)
        }
        invalidate()
    }

    fun start(particleSystem: ParticleSystem) {
        systems.add(particleSystem)
        invalidate()
    }

    var fps: Int = 0
    var fpsCounter: Int = 0
    var fpsTime: Long = 0
    fun calcFps() {
        if (SystemClock.uptimeMillis() - fpsTime > 1000) {
            fpsTime = SystemClock.uptimeMillis()
            fps = fpsCounter
            fpsCounter = 0
        } else {
            fpsCounter++
        }
    }
}
