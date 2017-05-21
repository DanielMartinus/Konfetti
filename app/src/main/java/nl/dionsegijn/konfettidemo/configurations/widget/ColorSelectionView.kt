package nl.dionsegijn.konfettidemo.configurations.widget

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
class ColorSelectionView(context: Context?) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_color_selection, this)
        orientation = HORIZONTAL
    }

    fun setColors(colors: IntArray) {
        colors.forEach { color ->
            val view = View(context)
            view.layoutParams = LinearLayout.LayoutParams(80, 60)
            view.setBackgroundColor(color)
            addView(view)
        }
    }
}
