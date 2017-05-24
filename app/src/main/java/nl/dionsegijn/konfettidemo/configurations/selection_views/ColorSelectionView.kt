package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_section_color_selection.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration


/**
 * Created by dionsegijn on 5/21/17.
 */
class ColorSelectionView(context: Context?) : LinearLayout(context), UpdateConfiguration {


    init {
        inflate(context, R.layout.view_section_color_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        addColorsToViewGroup(colorRow1, intArrayOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple, R.color.lt_pink))
        addColorsToViewGroup(colorRow2, intArrayOf(R.color.dk_blue, R.color.dk_cyan, R.color.dk_green, R.color.dk_red))
    }

    fun addColorsToViewGroup(viewGroup: LinearLayout, colors: IntArray) {
        colors.forEachIndexed { index, color ->
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

            viewGroup.addView(view)
        }
    }

    private fun pxFromDp(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    override fun onUpdateConfiguration(configuration: Configuration) {
    }
}
