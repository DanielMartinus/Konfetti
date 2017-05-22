package nl.dionsegijn.konfettidemo.configurations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
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

    private var point1 = 0.0
    private var point2 = 0.0
    private var selected: String? = null

    private var centerX: Float = 0f
    private var centerY:Float = 0f
    private var startX: Float = 0f
    private var startY:Float = 0f
    private var startDirection: Float? = null
    private val size: Float = 20f

    private fun touchStart(x: Float, y: Float) {
        centerX = measuredWidth / 2f
        centerY = measuredHeight / 2f
        val r = centerX - size
        startX = x
        startY = y

        val p1 = calculateXY(centerX, centerY, r, Math.toRadians(point1))
        val p2 = calculateXY(centerX, centerY, r, Math.toRadians(point2))
        if(insideCircle(startX, startY, p1.x, p1.y, size)) {
            selected = "point1"
        } else if(insideCircle(startX, startY, p2.x, p2.y, size)) {
            selected = "point2"
        }
    }

    fun insideCircle(x: Float, y: Float, cx: Float, cy: Float, r: Float): Boolean {
        return Math.pow((x - cx).toDouble(), 2.0) + Math.pow((y - cy).toDouble(), 2.0) < Math.pow(r.toDouble(), 2.0)
    }

    private fun touchMove(x: Float, y: Float) {
        if(startDirection == null) {
            startDirection = if(selected == "point1") point1.toFloat() else point2.toFloat()
        }
        val angle = calculateAngle(centerX, centerY, startX, startY, x, y).toFloat()
        val degrees = Math.toDegrees(angle.toDouble()) * -1 + startDirection!!
        if(selected == "point1") {
            point1 = degrees
        } else if(selected == "point2") {
            point2 = degrees
        }
        this.invalidate()
    }

    fun calculateAngle(centerX: Float, centerY: Float, x1: Float,
                       y1: Float, x2: Float, y2: Float): Double {
        var angle1 = Math.atan2((y1 - centerY).toDouble(), (x1 - centerX).toDouble())
        if(angle1 < 0) angle1 += 2 * Math.PI
        var angle2 = Math.atan2((y2 - centerY).toDouble(), (x2 - centerX).toDouble())
        if(angle2 < 0) angle2 += 2 * Math.PI
        return angle1 - angle2
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                touchStart(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                startDirection = null
                selected = null
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(measuredHeight ==  0 &&  measuredWidth == 0) return

        val cx: Float = measuredHeight / 2f
        val cy: Float = measuredWidth / 2f
        val r: Float = cx - size

        val r1 = Math.toRadians(point1)
        val r2 = Math.toRadians(point2)

        canvas.drawCircle(cx, cy, r, paintCircleStroke)

        val p1 = calculateXY(cx, cy, r, r1)
        val p2 = calculateXY(cx, cy, r, r2)

        canvas.drawCircle(p1.x, p1.y, size, paintCircleSolid)
        canvas.drawCircle(p2.x, p2.y, size, paintCircleSolid)
    }

    fun calculateXY(cx: Float, cy: Float, r: Float, angle: Double): PointF {
        val x1 = cx + r * Math.cos(angle)
        val y1 = cy + r * Math.sin(angle)
        return PointF(x1.toFloat(), y1.toFloat())
    }

}
