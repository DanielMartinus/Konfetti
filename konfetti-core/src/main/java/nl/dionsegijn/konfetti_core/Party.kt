package nl.dionsegijn.konfetti_core

import android.graphics.Color
import nl.dionsegijn.konfetti_core.NewEmitter.EmitterConfig
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size

/**
 * Configuration how to confetti should be rendered
 * TODO add property documentation
 */
data class Party(
    val angle: Int = 0,
    val spread: Int = 20,
    val startVelocity: Velocity = Velocity(20f),
    val size: List<Size> = listOf(Size.SMALL, Size.MEDIUM, Size(10)),
    val colors: List<Int> = listOf(Color.RED),
    val shapes: List<Shape> = listOf(Shape.Square, Shape.Circle),
    val timeToLive: Long = 2000, // milliseconds
    val fadeOutEnabled: Boolean = true,
    val position: Position = Position.xy(100f, 100f),
    val delay: Int = 0,
    val speedDensityIndependent: Boolean = true,
    val accelerationEnabled: Boolean = true,
    val maxAcceleration: Float = -1f, // TODO divide maxAcceleration by 10 in Confetti
    val rotation: Rotation = Rotation(),
    val emitter: EmitterConfig
)

data class Velocity(val min: Float, val max: Float? = null)

sealed class Position {
    data class xy(val x: Float, val y: Float): Position() {
        fun between(value: xy): Position = between(this, value)
    }
    data class relative(val x: Double, val y: Double): Position() {
        fun between(value: relative): Position = between(this, value)
    }
    internal data class between(val min: Position, val max: Position): Position()
}

data class Rotation(
    val enabled: Boolean = true,
    val baseRotationMultiplier: Float = 1f,
    val rotationVariance: Float = 0.2f
) {
    companion object {
        fun enabled() = Rotation(enabled = true)
        fun disabled() = Rotation(enabled = false)
    }
}

