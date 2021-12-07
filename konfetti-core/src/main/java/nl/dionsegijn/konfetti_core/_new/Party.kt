package nl.dionsegijn.konfetti_core._new

import android.graphics.Color
import nl.dionsegijn.konfetti_core._new.NewEmitter.EmitterConfig
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size

data class Party(
    val angle: Int = 0,
    val spread: Int = 20,
    val startVelocity: Int = 20, // Add min and max velocity
    val size: List<Size> = listOf(Size.SMALL, Size.MEDIUM, Size(10)),
    val colors: List<Int> = listOf(Color.RED),
    val shapes: List<Shape> = listOf(Shape.Square, Shape.Circle),
    val timeToLive: Long = 2000, // milliseconds
    val fadeOutEnabled: Boolean = true,
    val position: Position = Position(100f, 100f),
    val delay: Int = 0,
    val speedDensityIndependent: Boolean = true,
    val accelerationEnabled: Boolean = true,
    val maxAcceleration: Float = -1f, // TODO divide maxAcceleration by 10 in Confetti
    val rotation: Rotation = Rotation(),
    val emitter: EmitterConfig
)

// TODO improve usage for JAVA code
class PartyFactory {
    @JvmOverloads
    fun createParty(
        angle: Int = 0,
        spread: Int = 20,
        startVelocity: Int = 20, // Add min and max velocity
        size: List<Size> = listOf(Size.SMALL, Size.MEDIUM, Size(10)),
        colors: List<Int> = listOf(Color.RED),
        shapes: List<Shape> = listOf(Shape.Square, Shape.Circle),
        timeToLive: Long = 2000, // milliseconds
        fadeOutEnabled: Boolean = true,
        position: Position = Position(100f, 100f),
        delay: Int = 0,
        speedDensityIndependent: Boolean = true,
        accelerationEnabled: Boolean = true,
        maxAcceleration: Float = -1f, // TODO divide maxAcceleration by 10 in Confetti
        rotation: Rotation = Rotation(),
        emitter: EmitterConfig
    ): Party = Party(
        angle,
        spread,
        startVelocity,
        size,
        colors,
        shapes,
        timeToLive,
        fadeOutEnabled,
        position,
        delay,
        speedDensityIndependent,
        accelerationEnabled,
        maxAcceleration,
        rotation,
        emitter
    )
}

// TODO Support relative position
data class Position(val x: Float, val y: Float)

data class Rotation(
    val enabled: Boolean = true,
    val baseRotationMultiplier: Float = 1f,
    val rotationVariance: Float = 0.2f
) {
    companion object {
        fun disabled() = Rotation(enabled = false)
    }
}
