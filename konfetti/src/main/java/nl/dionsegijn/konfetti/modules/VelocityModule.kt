package nl.dionsegijn.konfetti.modules

import nl.dionsegijn.konfetti.models.Vector
import java.util.Random

/**
 * Velocity module to calculate the angular velocity
 * Based on the given angle(s) and speed [getVelocity] returns a [Vector]
 */
class VelocityModule(private val random: Random) {

    /**
     * Minimum angle as Radian
     */
    var minAngle: Double = 0.0

    /**
     * Maximum angle as Radian
     * Once set it will be used to randomize between [minAngle] and maxAngle when velocity
     * is being calculated
     */
    var maxAngle: Double? = null

    /**
     * Minimum speed (magnitude)
     * Speed lower than 0 will automatically set the speed to 0
     */
    var minSpeed: Float = 0f
        set(value) {
            field = if (value < 0) 0f else value
        }

    /**
     * Maximum speed (magnitude)
     * Once set it will be used to randomize between [minSpeed] and maxSpeed when velocity
     * is being calculated
     * Speed lower than 0 will automatically set the speed to 0
     */
    var maxSpeed: Float? = null
        set(value) {
            field = if (value!! < 0) 0f else value
        }

    var maxAcceleration: Float = -1f

    /**
     * Minimum rotation speed multiplier
     */
    var baseRotationMultiplier: Float = 1f

    /**
     * Variance in rotation speed from the base multiplier given in percent
     */
    var rotationVariance: Float = 0.2f

    /**
     * If both minSpeed and maxSpeed are set a random speed between those values will be returned
     * Otherwise only the minimum speed will be returned
     * return magnitude (speed)
     */
    fun getSpeed(): Float {
        return if (maxSpeed == null) {
            minSpeed
        } else {
            ((maxSpeed!! - minSpeed) * random.nextFloat()) + minSpeed
        }
    }

    /**
     * If both the minimum angle and maximum angle are set a random radian will be returned
     * otherwise only the minimum radian will be returned
     * return angle in radians
     */
    fun getRadian(): Double {
        return if (maxAngle == null) {
            minAngle
        } else {
            ((maxAngle!! - minAngle) * random.nextDouble()) + minAngle
        }
    }

    /**
     * Calculate velocity based on radian and speed
     * return [Vector] velocity
     */
    fun getVelocity(): Vector {
        val speed = getSpeed()
        val radian = getRadian()
        val vx = speed * Math.cos(radian).toFloat()
        val vy = speed * Math.sin(radian).toFloat()
        return Vector(vx, vy)
    }

    /**
     * Calculate a rotation speed multiplier based on the base and variance
     * return float multiplier
     */
    fun getRotationSpeedMultiplier(): Float {
        val randomValue = random.nextFloat() * 2f - 1f
        return baseRotationMultiplier + (baseRotationMultiplier * rotationVariance * randomValue)
    }
}
