package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_section_multi_seekbar_selection.view.*
import me.bendik.simplerangeview.SimpleRangeView
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration


/**
 * Created by dionsegijn on 5/21/17.
 */
@SuppressLint("ViewConstructor")
class MultiSeekbarSelectionView(
        context: Context,
        configuration: ConfigurationManager,
        val title: String,
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

        rangebar.start = minStartValue
        rangebar.end = maxStartValue
        rangebar.count = max + 1

        rangebar.onTrackRangeListener = object : SimpleRangeView.OnTrackRangeListener {
            override fun onEndRangeChanged(rangeView: SimpleRangeView, end: Int) {
                viewSeekbarTitle.text = createString(title, rangeView.start, end)
                onMultiSeekBarValueChanged.onValueChanged(rangeView.start.toFloat(), end.toFloat())
            }

            override fun onStartRangeChanged(rangeView: SimpleRangeView, start: Int) {
                viewSeekbarTitle.text = createString(title, start, rangeView.end)
                onMultiSeekBarValueChanged.onValueChanged(start.toFloat(), rangeView.end.toFloat())
            }
        }

        viewSeekbarTitle.text = createString(title, minStartValue, maxStartValue)
    }

    fun createString(title: String, value1: Int, value2: Int): String = "$title ($value1, $value2)"

    override fun onUpdateConfiguration(configuration: Configuration) {
        rangebar.start = configuration.minSpeed.toInt()
        rangebar.end = configuration.maxSpeed.toInt()
        viewSeekbarTitle.text = createString(title, rangebar.start, rangebar.end)
    }

}
