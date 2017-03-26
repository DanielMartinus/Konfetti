package nl.dionsegijn.konfetti

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View


/**
 * Created by dionsegijn on 3/25/17.
 */
class KonfettiView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val konfettiSystems: MutableList<ParticleSystem> = mutableListOf()

    fun build(): ParticleSystem {
        return ParticleSystem(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val it = konfettiSystems.iterator()
        while (it.hasNext()) {
            val konfetti = it.next()
            konfetti.render(canvas)
        }
        invalidate()
    }

    fun start(particleSystem: ParticleSystem) {
        konfettiSystems.add(particleSystem)
        particleSystem.startTimer()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
