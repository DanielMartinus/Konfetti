package nl.dionsegijn.konfettidemo.configurations.settings

import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
open class Configuration(val title: String) {

    var timeToLive: Long = 2000
    var minSpeed: Float = 1f
    var maxSpeed: Float = 5f
    var colors = intArrayOf(R.color.lt_yellow, R.color.lt_orange, R.color.lt_purple, R.color.lt_pink)
    var shapes: Array<Shape> = arrayOf(Shape.RECT, Shape.CIRCLE)
}
