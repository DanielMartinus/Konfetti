package nl.dionsegijn.konfettidemo.configurations.selection_views

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.view_section_seekbar_selection.view.*
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
class SeekbarSelectionView(context: Context?,
                                title: String,
                                min: Int,
                                max: Int,
                                startValue: Int) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_seekbar_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER

        viewSeekbar.max = max - min
        viewSeekbar.progress = startValue
        viewSeekbarTitle.text = "$title ($startValue)"
        viewSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + min
                viewSeekbarTitle.text = "$title ($value)"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

    }

}
