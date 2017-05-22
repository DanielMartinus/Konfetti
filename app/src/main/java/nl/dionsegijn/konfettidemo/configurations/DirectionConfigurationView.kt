package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by dionsegijn on 5/21/17.
 */
class DirectionConfigurationView : View {

    var paintCircleStroke: Paint

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        paintCircleStroke = Paint()
        paintCircleStroke.color = Color.RED
        paintCircleStroke.style = Paint.Style.STROKE
        paintCircleStroke.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(measuredHeight ==  0 && measuredWidth == 0) return

        canvas.drawCircle(measuredHeight / 2f, measuredWidth / 2f, 50f, paintCircleStroke)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(widthMeasureSpec > 0 || heightMeasureSpec > 0) {
            invalidate()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
