package nl.dionsegijn.konfetti_core

import nl.dionsegijn.konfetti_core.NewEmitter.EmitterConfig
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size

/**
 * Factory class to enable builder methods for Java implementations
 */
class PartyFactory(val emitter: EmitterConfig) {

    private var party: Party = Party(emitter = emitter)

    fun angle(angle: Int): PartyFactory {
        party = party.copy(angle = angle)
        return this
    }

    fun spread(spread: Int): PartyFactory {
        party = party.copy(spread = spread)
        return this
    }

    fun setSpeed(speed: Float): PartyFactory {
        party = party.copy(speed = speed)
        return this
    }

    fun setSpeedBetween(minSpeed: Float, maxSpeed: Float): PartyFactory {
        party = party.copy(speed = minSpeed, maxSpeed = maxSpeed)
        return this
    }

    fun position(position: Position): PartyFactory {
        party = party.copy(position = position)
        return this
    }

    fun position(x: Float, y: Float): PartyFactory {
        party = party.copy(position = Position.xy(x, y))
        return this
    }

    fun position(minX: Float, minY: Float, maxX: Float, maxY: Float): PartyFactory {
        party = party.copy(position = Position.xy(minX, minY).between(Position.xy(maxX, maxY)))
        return this
    }

    fun position(x: Double, y: Double): PartyFactory {
        party = party.copy(position = Position.relative(x, y))
        return this
    }

    fun position(minX: Double, minY: Double, maxX: Double, maxY: Double): PartyFactory {
        party = party.copy(
            position = Position.relative(minX, minY).between(Position.relative(maxX, maxY))
        )
        return this
    }

    fun sizes(vararg sizes: Size): PartyFactory {
        party = party.copy(size = sizes.toList())
        return this
    }

    fun sizes(size: List<Size>): PartyFactory {
        party = party.copy(size = size)
        return this
    }

    fun colors(colors: List<Int>): PartyFactory {
        party = party.copy(colors = colors)
        return this
    }

    fun shapes(shapes: List<Shape>): PartyFactory {
        party = party.copy(shapes = shapes)
        return this
    }

    fun shapes(vararg shapes: Shape): PartyFactory {
        party = party.copy(shapes = shapes.toList())
        return this
    }

    fun timeToLive(timeToLive: Long): PartyFactory {
        party = party.copy(timeToLive = timeToLive)
        return this
    }

    fun fadeOutEnabled(fadeOutEnabled: Boolean): PartyFactory {
        party = party.copy(fadeOutEnabled = fadeOutEnabled)
        return this
    }

    fun delay(delay: Int): PartyFactory {
        party = party.copy(delay = delay)
        return this
    }

    fun accelerationEnabled(accelerationEnabled: Boolean): PartyFactory {
        party = party.copy(accelerationEnabled = accelerationEnabled)
        return this
    }

    fun maxAcceleration(maxAcceleration: Float): PartyFactory {
        party = party.copy(maxAcceleration = maxAcceleration)
        return this
    }

    fun rotation(rotation: Rotation): PartyFactory {
        party = party.copy(rotation = rotation)
        return this
    }

    fun build(): Party = party
}
