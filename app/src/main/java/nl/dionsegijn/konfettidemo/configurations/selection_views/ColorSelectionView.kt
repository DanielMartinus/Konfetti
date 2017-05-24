package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import nl.dionsegijn.konfettidemo.R



/**
 * Created by dionsegijn on 5/21/17.
 */
class ColorSelectionView(context: Context?) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_color_selection, this)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL
        setColors(intArrayOf(R.color.yellow, R.color.orange, R.color.purple, R.color.pink))
    }

    fun setColors(colors: IntArray) {
        colors.forEach { color ->
            val view = Button(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = 6f
            }

            val params = LinearLayout.LayoutParams(pxFromDp(40f).toInt(), pxFromDp(25f).toInt())
            val margin = pxFromDp(12f).toInt()
            params.setMargins(margin, pxFromDp(24f).toInt(), margin, margin)
            view.layoutParams = params

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.setColor(ContextCompat.getColor(context, color))
            shape.setStroke(4, Color.WHITE)
            shape.cornerRadius = 6f

            view.background = shape

            addView(view)
        }
    }

    private fun pxFromDp(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
