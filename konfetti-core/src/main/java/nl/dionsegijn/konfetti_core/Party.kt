package nl.dionsegijn.konfetti_core

import android.graphics.Color
import nl.dionsegijn.konfetti_core.NewEmitter.Emitter
import nl.dionsegijn.konfetti_core.NewEmitter.EmitterConfig
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size
import java.util.concurrent.TimeUnit

/**
 * Configuration how to confetti should be rendered
 * TODO add property documentation
 */
data class Party(
    val angle: Int = 0,
    val spread: Int = 20,
    val startVelocity: Int = 20, // Add min and max velocity
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

// TODO Support relative position
sealed class Position {
    data class xy(val x: Float, val y: Float): Position() {
        fun between(value: xy): Position = between(this, value)
    }
    data class relative(val x: Double, val y: Double): Position() {
        fun between(value: relative): Position = between(this, value)
    }
    internal data class between(val first: Position, val second: Position): Position()
}

fun test() {
    Party(
        startVelocity = 5,
        angle = 270, // TOP
        spread = 30,
        timeToLive = 3000L,
        colors = listOf(0xfce18a, 0xff726d),
        emitter = Emitter(duration = 1L, TimeUnit.SECONDS).max(300),
        position = Position.relative(0.5, 0.5).between(Position.relative(0.6, 0.5))
    )
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
        position: Position = Position.xy(100f, 100f),
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
