package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageButton
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration

/**
 * Created by dionsegijn on 5/21/17.
 */
class ShapeSelectionView(
        context: Context,
        private val configurationManager: ConfigurationManager) : LinearLayout(context), UpdateConfiguration {

    private val buttonWidth = pxFromDp(100f).toInt()
    private val buttonHeight = pxFromDp(60f).toInt()
    private val margin = pxFromDp(8f).toInt()

    private fun pxFromDp(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private val availableShapes = arrayOf(Shape.CIRCLE, Shape.RECT)

    init {
        inflate(context, R.layout.view_section_shape_selection, this)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        displayShapeConfigOptions(availableShapes)
    }

    private fun displayShapeConfigOptions(selectedShapes: Array<Shape>) {
        availableShapes.forEach { shape ->
            val button = AppCompatImageButton(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.elevation = 6f
            }

            val drawable = if (shape == Shape.RECT) R.drawable.ic_rectangle else R.drawable.ic_circle

            /** Set width, height and margins of the button */
            val params = LinearLayout.LayoutParams(buttonWidth, buttonHeight)
            params.setMargins(margin, margin, margin, margin)
            params.gravity = Gravity.CENTER
            button.layoutParams = params

            setButtonState(button, selectedShapes.contains(shape), drawable)
            button.setOnClickListener { v ->
                val tempList = configurationManager.active.shapes.toMutableList()
                val isNotSelected = !tempList.contains(shape)
                if (isNotSelected) {
                    tempList.add(shape)
                } else {
                    if (tempList.size != 1) {
                        tempList.remove(shape)
                    } else {
                        Toast.makeText(context, context.getString(R.string.select_shape_minimal_one_warning), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                setButtonState(v as AppCompatImageButton, isNotSelected, drawable)
                configurationManager.active.shapes = tempList.toTypedArray()
            }

            addView(button)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setButtonState(button: AppCompatImageButton, isSelected: Boolean, @DrawableRes resDrawable: Int) {
        val foregroundColor = if (isSelected) 0xfff1f1f1.toInt() else 0xffacacac.toInt()
        val backgroundColor = if (isSelected) 0xffffce08.toInt() else 0xfff1f1f1.toInt()
        val colorStateList = ColorStateList(arrayOf(IntArray(0)), intArrayOf(backgroundColor))
        button.supportBackgroundTintList = colorStateList

        val drawable = ContextCompat.getDrawable(context, resDrawable)
        drawable?.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_IN)
        button.setImageDrawable(drawable)
    }

    override fun onUpdateConfiguration(configuration: Configuration) {
        removeAllViews()
        displayShapeConfigOptions(configuration.shapes)
    }

}
