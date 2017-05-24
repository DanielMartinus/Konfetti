package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_section_color_selection.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.OnConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration


/**
 * Created by dionsegijn on 5/21/17.
 * Simple widget view showing two rows with 4 colors
 * Call any configuration change when a color is selected and update its shape to reflect the
 * state of the view
 */
@SuppressLint("ViewConstructor")
class ColorSelectionView(context: Context?,
                         val onConfigurationChangedListener: OnConfigurationChangedListener,
                         val configurationManager: ConfigurationManager) : LinearLayout(context), UpdateConfiguration {

    val availableColors = listOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple,
            R.color.lt_pink, R.color.dk_blue, R.color.dk_cyan, R.color.dk_green, R.color.dk_red)
    var selectedColors: MutableList<Int> = mutableListOf()

    init {
        inflate(context, R.layout.view_section_color_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        addColorsToViewGroup(colorRow1, availableColors.take(4).toIntArray(), configurationManager.active.colors)
        addColorsToViewGroup(colorRow2, availableColors.takeLast(4).toIntArray(), configurationManager.active.colors)
    }

    fun addColorsToViewGroup(viewGroup: LinearLayout, colors: IntArray, initSelectedColors: IntArray) {
        colors.forEach { color ->
            val view = Button(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = 6f
            }

            val params = LinearLayout.LayoutParams(pxFromDp(40f).toInt(), pxFromDp(25f).toInt())
            val margin = pxFromDp(12f).toInt()
            params.setMargins(margin, pxFromDp(24f).toInt(), margin, margin)
            view.layoutParams = params

            setStateButton(view, color, initSelectedColors.contains(color))

            view.setOnClickListener { v ->
                setStateButton(v, color, !selectedColors.contains(color))
                configurationManager.active.colors = selectedColors.toIntArray()
            }

            viewGroup.addView(view)
        }
    }

    fun setStateButton(view: View, color: Int, selected: Boolean) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(ContextCompat.getColor(context, color))
        if(selected) {
            shape.setStroke(4, 0xFF27D232.toInt())
            selectedColors.add(color)
        } else {
            shape.setStroke(4, Color.WHITE)
            selectedColors.remove(color)
        }
        shape.cornerRadius = 6f
        view.background = shape
    }

    private fun pxFromDp(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    override fun onUpdateConfiguration(configuration: Configuration) {
        colorRow1.removeAllViews()
        colorRow2.removeAllViews()
        addColorsToViewGroup(colorRow1, availableColors.take(4).toIntArray(), configuration.colors)
        addColorsToViewGroup(colorRow2, availableColors.takeLast(4).toIntArray(), configuration.colors)
    }
}
