package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.SeekBar
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.databinding.ViewSectionSeekbarSelectionBinding
import nl.dionsegijn.konfettidemo.interfaces.OnSimpleSeekBarChangeListener
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration

/**
 * Created by dionsegijn on 5/21/17.
 */
@SuppressLint("ViewConstructor")
class TimeToLiveSelectionView(
        context: Context,
        configuration: ConfigurationManager,
        title: String,
        max: Int
) : LinearLayout(context), UpdateConfiguration {

    private var binding: ViewSectionSeekbarSelectionBinding =
        ViewSectionSeekbarSelectionBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        binding.viewSeekbar.max = max

        val startValue = configuration.active.timeToLive.toInt()
        binding.viewSeekbar.progress = startValue
        binding.viewSeekbarTitle.text = "$title ($startValue)"

        binding.viewSeekbar.setOnSeekBarChangeListener(object : OnSimpleSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.viewSeekbarTitle.text = "$title ($progress)"
                configuration.active.timeToLive = progress.toLong()
            }
        })

    }

    override fun onUpdateConfiguration(configuration: Configuration) {
        binding.viewSeekbar.progress = configuration.timeToLive.toInt()
    }

}
