package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.view_section_color_selection.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration


/**
 * Created by dionsegijn on 5/21/17.
 * Simple widget view showing two rows with 4 colors
 * Call any configuration change when a color is selected and update its shape to reflect the
 * state of the view
 */
@SuppressLint("ViewConstructor")
class ColorSelectionView(
        context: Context,
        private val configurationManager: ConfigurationManager
) : LinearLayout(context), UpdateConfiguration {

    private val availableColors = listOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple,
            R.color.lt_pink, R.color.dk_blue, R.color.dk_cyan, R.color.dk_green, R.color.dk_red)
    private val buttonWidth = pxFromDp(40f).toInt()
    private val buttonHeight = pxFromDp(25f).toInt()
    private val buttonMargin = pxFromDp(12f).toInt()

    init {
        inflate(context, R.layout.view_section_color_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER
        updateRows(configurationManager.active)
    }

    private fun updateRows(configuration: Configuration) {
        addColorsToViewGroup(colorRow1, availableColors.take(4).toIntArray(), configuration.colors)
        addColorsToViewGroup(colorRow2, availableColors.takeLast(4).toIntArray(), configuration.colors)
    }

    private fun addColorsToViewGroup(viewGroup: LinearLayout, colors: IntArray, initSelectedColors: IntArray) {
        colors.forEach { color ->
            val view = Button(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.elevation = 6f
            }

            /** Set width, height and margins of the button */
            val params = LinearLayout.LayoutParams(buttonWidth, buttonHeight)
            val margin = buttonMargin
            params.setMargins(margin, margin, margin, margin)
            view.layoutParams = params

            /** Create GradientDrawable reflecting the state of the button */
            setStateButton(view, color, initSelectedColors.contains(color))

            view.setOnClickListener { v ->
                if (getActiveColors().size == 1 && getActiveColors().contains(color)) {
                    Toast.makeText(context, context.getString(R.string.select_color_minimal_one_warning), Toast.LENGTH_SHORT).show();
                    return@setOnClickListener
                }
                // Reverse isSelected for opposite behavior
                val isNotSelected = !getActiveColors().contains(color)
                setStateButton(v, color, isNotSelected)

                // Create mutable list in order to manipulate items and set new color list
                val tempColors = getActiveColors().toMutableList()
                if (isNotSelected) tempColors.add(color) else tempColors.remove(color)
                configurationManager.active.colors = tempColors.toIntArray()
            }

            viewGroup.addView(view)
        }
    }

    private fun getActiveColors(): IntArray {
        return configurationManager.active.colors
    }

    private fun setStateButton(view: View, color: Int, selected: Boolean) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(ContextCompat.getColor(context, color))
        shape.setStroke(4, if (selected) 0xFF27D232.toInt() else Color.WHITE)
        shape.cornerRadius = 6f
        view.background = shape
    }

    private fun pxFromDp(dp: Float): Float = dp * resources.displayMetrics.density

    override fun onUpdateConfiguration(configuration: Configuration) {
        colorRow1.removeAllViews()
        colorRow2.removeAllViews()
        updateRows(configuration)
    }
}
