package nl.dionsegijn.konfettidemo.configurations

import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

/**
 * Created by dionsegijn on 5/21/17.
 */
interface Configuration {

    fun getColors(): IntArray

    fun getMinDirection(): Double
    fun getMaxDirection(): Double

    fun getMinSpeed(): Float
    fun getMaxSpeed(): Float

    fun isFadeOutEnabled(): Boolean
    fun getTimeToLive(): Long
    fun getShapes(): Array<Shape>
    fun getSizes(): Array<Size>

}
