package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.view_section_seekbar_selection.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.OnSimpleSeekBarChangeListener
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration

/**
 * Created by dionsegijn on 5/21/17.
 */
@SuppressLint("ViewConstructor")
class SpeedSelectionView(
        context: Context,
        configuration: ConfigurationManager,
        title: String,
        max: Int
) : LinearLayout(context), UpdateConfiguration {

    init {
        inflate(context, R.layout.view_section_seekbar_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER

        viewSeekbar.max = max

        val minSpeed = configuration.active.minSpeed.toInt()
        val maxSpeed = configuration.active.maxSpeed.toInt()
        viewSeekbar.progress = maxSpeed
        viewSeekbarTitle.text = "$title ($minSpeed, $maxSpeed)"

        viewSeekbar.setOnSeekBarChangeListener(object : OnSimpleSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewSeekbarTitle.text = "$title ($minSpeed, $maxSpeed)"
                configuration.active.maxSpeed = progress.toFloat()
            }
        })

    }

    override fun onUpdateConfiguration(configuration: Configuration) {
        viewSeekbar.progress = configuration.timeToLive.toInt()
    }

}
