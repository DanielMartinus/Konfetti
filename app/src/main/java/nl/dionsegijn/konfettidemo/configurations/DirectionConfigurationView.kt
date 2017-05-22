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

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val paintCircleStroke: Paint = Paint()
    val paintCircleSolid: Paint = Paint()

    init {
        paintCircleStroke.color = Color.RED
        paintCircleStroke.style = Paint.Style.STROKE
        paintCircleStroke.strokeWidth = 5f

        paintCircleSolid.color = Color.RED
        paintCircleSolid.strokeWidth = 5f
    }

    private var centerX: Float = 0f
    private var centerY:Float = 0f
    private var direction = 0f
    private var startX: Float = 0f
    private var startY:Float = 0f
    private var startDirection = 0f

    private fun touchStart(x: Float, y: Float) {
        centerX = this.width / 2f
        centerY = this.height / 2f
        startX = x
        startY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val angle = calculateAngle(centerX, centerY, startX, startY, x,
                y).toFloat()
        direction = Math.toDegrees(angle.toDouble()).toFloat() * -1 + startDirection
        this.invalidate()
    }

    fun calculateAngle(centerX: Float, centerY: Float, x1: Float,
                       y1: Float, x2: Float, y2: Float): Double {
        val angle1 = Math.atan2((y1 - centerY).toDouble(), (x1 - centerX).toDouble())
        val angle2 = Math.atan2((y2 - centerY).toDouble(), (x2 - centerX).toDouble())
        return angle1 - angle2
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                startDirection = direction;
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(measuredHeight ==  0 &&  measuredWidth == 0) return

        val cx: Float = (measuredHeight / 2f)
        val cy: Float = (measuredWidth / 2f)
        val size: Float = 20f
        val r: Float = cx - size

        val r1 = Math.toRadians(direction.toDouble())
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
