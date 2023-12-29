package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.Angle.Companion.BOTTOM
import nl.dionsegijn.konfetti.core.Angle.Companion.LEFT
import nl.dionsegijn.konfetti.core.Angle.Companion.RIGHT
import nl.dionsegijn.konfetti.core.Angle.Companion.TOP
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size

/**
 * Configuration how to confetti should be rendered
 * @property angle the direction the confetti will shoot
 * @property spread how wide the confetti will shoot in degrees. Use 1 to shoot in a straight line
 * and 360 to form a circle
 * @property speed The start speed of the confetti at the time of creation. Also set [maxSpeed] to
 * apply a random speed between speed and maxSpeed.
 * @property maxSpeed when [maxSpeed] is set a random speed between [speed] and [maxSpeed] will be
 * chosen. Using randomness creates a more natural and realistic look to the confetti when animating.
 * @property damping The rate at which the speed will decrease right after shooting the confetti
 * @property size The size of the confetti. Use: Size.SMALL, MEDIUM or LARGE for default sizes or
 * create your custom size using a new instance of [Size].
 * @property colors List of colors that will be randomly picked from
 * @property shapes Set the shape of the confetti. Set multiple shapes and it will be randomly
 * assigned upon creation of the confetti. See [Shape] for possible shapes and custom drawables.
 * @property timeToLive the amount of time in milliseconds before a particle will stop rendering
 * or fade out if [fadeOutEnabled] is set to true.
 * @property fadeOutEnabled If true and a confetti disappears because it ran out of time (set with timeToLive)
 * it will slowly fade out. If set to falls it will instantly disappear from the screen.
 * @property position the point where the confetti will spawn relative to the canvas. Use absolute
 * coordinates with [Position.Absolute] or relative coordinates between 0.0 and 1.0 using [Position.Relative].
 * Spawn confetti on random positions using [Position.Between].
 * @property delay the amount of milliseconds to wait before the rendering of the confetti starts
 * @property rotation enable the 3D rotation of a Confetti. See [Rotation] class for the configuration
 * options. Easily enable or disable it using [Rotation].enabled() or [Rotation].disabled() and
 * control the speed of rotations.
 * @property emitter instructions how many and often a confetti particle should spawn.
 * Use Emitter(duration, timeUnit).max(amount) or Emitter(duration, timeUnit).perSecond(amount) to
 * configure the Emitter.
 */
data class Party(
    val angle: Int = 0,
    val spread: Int = 360,
    val speed: Float = 30f,
    val maxSpeed: Float = 0f,
    val damping: Float = 0.9f,
    val size: List<Size> = listOf(Size.SMALL, Size.MEDIUM, Size.LARGE),
    val colors: List<Int> = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
    val shapes: List<Shape> = listOf(Shape.Square, Shape.Circle),
    val timeToLive: Long = 2000,
    val fadeOutEnabled: Boolean = true,
    val position: Position = Position.Relative(0.5, 0.5),
    val delay: Int = 0,
    val rotation: Rotation = Rotation(),
    val emitter: EmitterConfig,
)

/**
 * Helper class for easily setting an angle based on easy understandable constants
 * [TOP] 270 degrees
 * [RIGHT] 0 degrees
 * [BOTTOM] 90 degrees
 * [LEFT] 180 degrees
 */
class Angle {
    companion object {
        const val TOP: Int = 270
        const val RIGHT: Int = 0
        const val BOTTOM: Int = 90
        const val LEFT: Int = 180
    }
}

/**
 * Helper class for for easily configuring [Spread] when creating a [Party]
 * These are presets but any custom amount will work within 0-360 degrees
 */
class Spread {
    companion object {
        const val SMALL: Int = 30
        const val WIDE: Int = 100
        const val ROUND: Int = 360
    }
}

sealed class Position {
    /**
     * Set absolute position on the x and y axis of the KonfettiView
     * @property x the x coordinate in pixels
     * @property y the y coordinate in pixels
     */
    data class Absolute(val x: Float, val y: Float) : Position() {
        fun between(value: Absolute): Position = Between(this, value)
    }

    /**
     * Set relative position on the x and y axis of the KonfettiView. Some examples:
     * [x: 0.0, y: 0.0] Top left corner
     * [x: 1.0, y: 0.0] Top right corner
     * [x: 0.0, y: 1.0] Bottom left corner
     * [x: 1.0, y: 1.0] Bottom right corner
     * [x: 0.5, y: 0.5] Center of the view
     *
     * @property x the relative x coordinate as a double
     * @property y the relative y coordinate as a double
     */
    data class Relative(val x: Double, val y: Double) : Position() {
        fun between(value: Relative): Position = Between(this, value)
    }

    /**
     * Use this if you want to spawn confetti between multiple locations. Use this with [Absolute]
     * and [Relative] to connect two points
     * Example: Relative(0.0, 0.0).between(Relative(1.0, 0.0))
     * This will spawn confetti from the full width and top of the view
     */
    internal data class Between(val min: Position, val max: Position) : Position()
}

/**
 * @property enabled by default true. Set to false to prevent the confetti from rotating
 * @property speed the rate at which the confetti will rotate per frame. Control the 2D and 3D rotation
 * separately using [multiplier2D] and [multiplier3D]
 * @property variance the margin in which the rotationSpeed can differ to add randomness
 * to the rotation speed of each confetti.
 * @property multiplier2D Multiplier controlling the speed of the rotation around the center of the
 * confetti. Set this value to 0 to disable the 2D rotation effect.
 * @property multiplier3D Multiplier controlling the 3D rotation of the confetti.
 */
data class Rotation(
    val enabled: Boolean = true,
    val speed: Float = 1f,
    val variance: Float = 0.5f,
    val multiplier2D: Float = 8f,
    val multiplier3D: Float = 1.5f,
) {
    companion object {
        fun enabled() = Rotation(enabled = true)

        fun disabled() = Rotation(enabled = false)
    }
}
