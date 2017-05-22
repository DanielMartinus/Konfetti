package nl.dionsegijn.konfettidemo.configurations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by dionsegijn on 5/21/17.
 */
class DirectionConfigurationView : View {

    val paintCircleStroke: Paint = Paint()
    val paintCircleSolid: Paint = Paint()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        paintCircleStroke.color = Color.RED
        paintCircleStroke.style = Paint.Style.STROKE
        paintCircleStroke.strokeWidth = 5f

        paintCircleSolid.color = Color.RED
        paintCircleSolid.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(measuredHeight ==  0 &&  measuredWidth == 0) return

        val cx: Float = (measuredHeight / 2f)
        val cy: Float = (measuredWidth / 2f)
        val size: Float = 20f
        val r: Float = cx - size

        val r1 = Math.toRadians(90.0)
        val r2 = Math.toRadians(270.0)

        canvas.drawCircle(cx, cy, cx - size, paintCircleStroke)

        val x1 = cx + r * Math.cos(r1)
        val y1 = cy + r * Math.sin(r1)
        val x2 = cx + r * Math.cos(r2)
        val y2 = cy + r * Math.sin(r2)

        canvas.drawCircle(x1.toFloat(), y1.toFloat(), size, paintCircleSolid)
        canvas.drawCircle(x2.toFloat(), y2.toFloat(), size, paintCircleSolid)
    }

}
