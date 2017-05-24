package nl.dionsegijn.konfettidemo.interfaces

import android.widget.SeekBar

/**
 * Created by dionsegijn on 5/24/17.
 */
open class OnSimpleSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

}
