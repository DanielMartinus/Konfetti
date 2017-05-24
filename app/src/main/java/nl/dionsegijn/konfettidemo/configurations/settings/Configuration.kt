package nl.dionsegijn.konfettidemo.configurations.settings

import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
open class Configuration(val title: String) {

    var timeToLive: Long = 2000
    var colors = intArrayOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple, R.color.lt_pink)

}
