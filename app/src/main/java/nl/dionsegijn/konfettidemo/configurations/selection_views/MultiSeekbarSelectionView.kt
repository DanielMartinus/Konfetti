package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_section_multi_seekbar_selection.view.*
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
class MultiSeekbarSelectionView(context: Context?,
                                title: String,
                                min: Int,
                                max: Int,
                                minStartValue: Int,
                                maxStartValue: Int) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_multi_seekbar_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER

        viewMultiSeekbar.min = min
        viewMultiSeekbar.max = max
        viewMultiSeekbar.getThumb(0).value = minStartValue
        viewMultiSeekbar.getThumb(1).value = maxStartValue
        viewSeekbarTitle.text = createString(title, minStartValue, maxStartValue)
        viewMultiSeekbar.setOnThumbValueChangeListener { multiSlider, _, _, _ ->
            val value1 = multiSlider.getThumb(0).value
            val value2 = multiSlider.getThumb(1).value
            viewSeekbarTitle.text = createString(title, value1, value2)
        }
    }

    fun createString(title: String, value1: Int, value2: Int): String {
        return "$title ($value1, $value2)"
    }

}
