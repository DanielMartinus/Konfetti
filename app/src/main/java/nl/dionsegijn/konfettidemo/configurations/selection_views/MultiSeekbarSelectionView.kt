package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_section_multi_seekbar_selection.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration

/**
 * Created by dionsegijn on 5/21/17.
 */
@SuppressLint("ViewConstructor")
class MultiSeekbarSelectionView(context: Context?,
                                configuration: ConfigurationManager,
                                title: String,
                                min: Int,
                                max: Int,
                                val onMultiSeekBarValueChanged: OnMultiSeekBarValueChanged) : LinearLayout(context), UpdateConfiguration {

    interface OnMultiSeekBarValueChanged {
        fun onValueChanged(min: Float, max: Float)
    }

    init {
        inflate(context, R.layout.view_section_multi_seekbar_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER

        val minStartValue = configuration.active.minSpeed.toInt()
        val maxStartValue = configuration.active.maxSpeed.toInt()

        viewMultiSeekbar.min = min
        viewMultiSeekbar.max = max
        viewMultiSeekbar.getThumb(0).value = minStartValue
        viewMultiSeekbar.getThumb(1).value = maxStartValue
        viewSeekbarTitle.text = createString(title, minStartValue, maxStartValue)
        viewMultiSeekbar.setOnThumbValueChangeListener { multiSlider, thumb, _, _ ->
            val value1 = multiSlider.getThumb(0).value
            val value2 = multiSlider.getThumb(1).value
            viewSeekbarTitle.text = createString(title, value1, value2)
            /** Little work around so I know if the thumb value is set by the user or programmatically */
            if(thumb.isEnabled) {
                onMultiSeekBarValueChanged.onValueChanged(value1.toFloat(), value2.toFloat())
            } else {
                thumb.isEnabled = true
            }
        }
    }

    fun createString(title: String, value1: Int, value2: Int): String {
        return "$title ($value1, $value2)"
    }

    override fun onUpdateConfiguration(configuration: Configuration) {
        val thumb1 = viewMultiSeekbar.getThumb(0)
        thumb1.isEnabled = false
        val thumb2 = viewMultiSeekbar.getThumb(1)
        thumb2.isEnabled = false
        thumb1.value = configuration.minSpeed.toInt()
        thumb2.value = configuration.maxSpeed.toInt()
    }

}
