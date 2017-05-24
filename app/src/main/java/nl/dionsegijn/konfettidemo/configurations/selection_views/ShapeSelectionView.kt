package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.support.v7.widget.AppCompatButton
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
class ShapeSelectionView(context: Context?,
                         val configurationManager: ConfigurationManager) : LinearLayout(context), UpdateConfiguration {

    val selectedColor: Int = 0xffffce08.toInt()
    val defaultColor: Int = 0xfff1f1f1.toInt()

    val availableShapes = arrayOf(Shape.CIRCLE, Shape.RECT)

    init {
        inflate(context, R.layout.view_section_shape_selection, this)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        displayShapeConfigOptions(availableShapes)
    }

    fun displayShapeConfigOptions(selectedShapes: Array<Shape>) {
        availableShapes.forEach { shape ->
            val button = AppCompatButton(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.elevation = 6f
            }

            setButtonState(button, selectedShapes.contains(shape))
            button.setOnClickListener { v ->
                val tempList = configurationManager.active.shapes.toMutableList()
                val isNotSelected = !tempList.contains(shape)
                if(isNotSelected) {
                    tempList.add(shape)
                } else {
                    if(tempList.size != 1) {
                        tempList.remove(shape)
                    } else {
                        Toast.makeText(context, "Can't turn off al the shapes", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                setButtonState(v as AppCompatButton, isNotSelected)
                configurationManager.active.shapes = tempList.toTypedArray()
            }

            addView(button)
        }
    }

    fun setButtonState(button: AppCompatButton, isSelected: Boolean) {
        val color = if(isSelected) selectedColor else defaultColor
        val colorStateList = ColorStateList(arrayOf(IntArray(0)), intArrayOf(color))
        button.supportBackgroundTintList = colorStateList
    }

    override fun onUpdateConfiguration(configuration: Configuration) {
    }

}
