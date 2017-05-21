package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
class ShapeSelectionView(context: Context?) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_shape_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
    }

}
