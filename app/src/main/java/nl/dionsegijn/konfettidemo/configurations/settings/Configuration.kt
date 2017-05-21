package nl.dionsegijn.konfettidemo.configurations.settings

/**
 * Created by dionsegijn on 5/21/17.
 */
interface Configuration {

    /** Confetti configurations */

    fun getColors(): IntArray

    fun getMinDirection(): Double
    fun getMaxDirection(): Double

    fun getMinSpeed(): Float
    fun getMaxSpeed(): Float

    fun isFadeOutEnabled(): Boolean
    fun getTimeToLive(): Long
    fun getShapes(): Array<nl.dionsegijn.konfetti.models.Shape>
    fun getSizes(): Array<nl.dionsegijn.konfetti.models.Size>

    /** View configurations */

    // TODO add text for view
}
