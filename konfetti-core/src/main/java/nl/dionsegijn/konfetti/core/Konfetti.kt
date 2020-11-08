package nl.dionsegijn.konfetti.core

import android.graphics.Color
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size

class Konfetti {

    companion object {
        @JvmStatic
        fun build(): Configuration {
            return Configuration()
        }
    }
}

data class Configuration(
    val minX: Float = 0f,
    val maxX: Float? = null,
    val minY: Float = 0f,
    val maxY: Float? = null,
    val minAngle: Double = 0.0,
    val maxAngle: Double? = null,
    val minSpeed: Float = 0f,
    val maxSpeed: Float? = null,
    val maxAcceleration: Float = -1f,
    val baseRotationMultiplier: Float = 1f,
    val rotationVariance: Float = 0.2f,
    val colors: List<Int> = listOf(Color.RED),
    val sizes: List<Size> = emptyList(),
    val shapes: List<Shape> = emptyList(),
    val fadeOut: Boolean = false,
    val timeToLive: Long = 2000L,
    val rotate: Boolean = true,
    val accelerate: Boolean = true
) {

    fun addSizes(vararg sizes: Size) = copy(sizes = sizes.toList())

    fun addShapes(vararg shapes: Shape) = copy(shapes = shapes.toList())

    fun addColors(vararg colors: Int) = copy(colors = colors.toList())

    fun addColors(colors: List<Int>) = copy(colors = colors)

    /**
     * Set position to emit particles from
     */
    fun setPosition(x: Float, y: Float) = copy(
        minX = x, minY = y
    )

    /**
     * Set position range to emit particles from
     * A random position on the x-axis between [minX] and [maxX] and y-axis between [minY] and [maxY]
     * will be picked for each confetti.
     * @param [maxX] leave this null to only emit from [minX]
     * @param [maxY] leave this null to only emit from [minY]
     */
    fun setPosition(
        minX: Float,
        maxX: Float? = null,
        minY: Float,
        maxY: Float? = null
    ) = copy(minX = minX, maxX = maxX, minY = minY, maxY = maxY)

    /**
     * Set direction you want to have the particles shoot to
     * [degrees] direction in degrees ranging from 0 - 360
     * Default degrees is 0 which starts at the right side
     */
    fun setDirection(degrees: Double) = copy(
        minAngle = Math.toRadians(degrees)
    )

    /**
     * Set direction you want to have the particles shoot to
     * [minDegrees] direction in degrees
     * [maxDegrees] direction in degrees
     * Default minDegrees is 0 which starts at the right side
     * maxDegrees is by default not set
     */
    fun setDirection(minDegrees: Double, maxDegrees: Double) = copy(
        minAngle = Math.toRadians(minDegrees),
        maxAngle = Math.toRadians(maxDegrees)
    )

    /**
     * Set the speed of the particle
     * If value is negative it will be automatically set to 0
     */
    fun setSpeed(speed: Float) = copy(
        minSpeed = speed
    )

    /**
     * Set the speed range of the particle
     * If one of the values is negative it will be automatically set to 0
     */
    fun setSpeed(minSpeed: Float, maxSpeed: Float) = copy(
        minSpeed = minSpeed,
        maxSpeed = maxSpeed
    )

    fun setMaxAcceleration(acceleration: Float) = copy(
        maxAcceleration = acceleration
    )

    fun setRotationSpeedMultiplier(multiplier: Float): Configuration {
        require(multiplier >= 0f) { "multiplier ($multiplier) must be greater or equal to 0" }
        return copy(baseRotationMultiplier = multiplier)
    }

    fun setRotationSpeedVariance(variance: Float): Configuration {
        require(variance in 0f..1f) { "variance ($variance) must be in the range 0..1" }
        return copy(rotationVariance = variance)
    }

    fun setFadeOutEnabled(fadeOut: Boolean) = copy(fadeOut = fadeOut)

    fun setTimeToLive(timeInMs: Long) = copy(timeToLive = timeInMs)

    fun setRotationEnabled(enabled: Boolean) = copy(rotate = enabled)

    fun setAccelerationEnabled(enabled: Boolean) = copy(accelerate = enabled)

}

