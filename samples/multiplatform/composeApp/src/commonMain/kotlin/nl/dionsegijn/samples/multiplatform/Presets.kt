package nl.dionsegijn.samples.multiplatform

import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Same presets as samples/shared, written with Duration so they run on every platform
object Presets {
    private val colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def)

    fun festive(image: Shape.DrawableShape? = null): List<Party> {
        val party =
            Party(
                speed = 30f,
                maxSpeed = 50f,
                damping = 0.9f,
                angle = Angle.TOP,
                spread = 45,
                size = listOf(Size.SMALL, Size.LARGE, Size.LARGE),
                shapes = listOfNotNull(Shape.Square, Shape.Circle, image),
                timeToLive = 3000L,
                rotation = Rotation(),
                colors = colors,
                emitter = Emitter(100.milliseconds).max(30),
                position = Position.Relative(0.5, 1.0),
            )

        return listOf(
            party,
            party.copy(speed = 55f, maxSpeed = 65f, spread = 10, emitter = Emitter(100.milliseconds).max(10)),
            party.copy(speed = 50f, maxSpeed = 60f, spread = 120, emitter = Emitter(100.milliseconds).max(40)),
            party.copy(speed = 65f, maxSpeed = 80f, spread = 10, emitter = Emitter(100.milliseconds).max(10)),
        )
    }

    fun explode(): List<Party> =
        listOf(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = colors,
                emitter = Emitter(100.milliseconds).max(100),
                position = Position.Relative(0.5, 0.3),
            ),
        )

    fun parade(): List<Party> {
        val party =
            Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                angle = Angle.RIGHT - 45,
                spread = Spread.SMALL,
                colors = colors,
                emitter = Emitter(5.seconds).perSecond(30),
                position = Position.Relative(0.0, 0.5),
            )

        return listOf(
            party,
            // Flip the angle from right to left
            party.copy(angle = party.angle - 90, position = Position.Relative(1.0, 0.5)),
        )
    }

    fun rain(): List<Party> =
        listOf(
            Party(
                speed = 0f,
                maxSpeed = 15f,
                damping = 0.9f,
                angle = Angle.BOTTOM,
                spread = Spread.ROUND,
                colors = colors,
                emitter = Emitter(5.seconds).perSecond(100),
                position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
            ),
        )

    fun shapes(): List<Party> =
        listOf(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                // A bit bigger than the default sizes, so the shapes are easy to tell apart
                size = listOf(Size(12), Size(16, mass = 6f)),
                shapes = listOf(Shape.Polygon.STAR, Shape.Polygon.TRIANGLE, Shape.Polygon.DIAMOND, Shape.Square),
                colors = colors,
                emitter = Emitter(100.milliseconds).max(100),
                position = Position.Relative(0.5, 0.3),
            ),
        )
}
