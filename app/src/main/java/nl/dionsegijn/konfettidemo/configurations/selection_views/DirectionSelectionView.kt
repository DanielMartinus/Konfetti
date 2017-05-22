package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
class DirectionSelectionView(context: Context?) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_direction_selection, this)
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
    }


}
