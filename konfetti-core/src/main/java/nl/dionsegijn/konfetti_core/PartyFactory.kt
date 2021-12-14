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

    fun setStartVelocity(velocity: Float): PartyFactory {
        party = party.copy(startVelocity = Velocity(velocity))
        return this
    }

    fun setStartVelocity(minVelocity: Float, maxVelocity: Float): PartyFactory {
        party = party.copy(startVelocity = Velocity(minVelocity, maxVelocity))
        return this
    }

    fun position(position: Position): PartyFactory {
        party = party.copy(position = position)
        return this
    }

    fun addSizes(vararg sizes: Size): PartyFactory {
        party = party.copy(size = sizes.toList())
        return this
    }

    fun addSizes(size: List<Size>): PartyFactory {
        party = party.copy(size = size)
        return this
    }

    fun colors(colors: List<Int>): PartyFactory {
        party = party.copy(colors = colors)
        return this
    }

    fun addShapes(shapes: List<Shape>): PartyFactory {
        party = party.copy(shapes = shapes)
        return this
    }

    fun addShapes(vararg shapes: Shape): PartyFactory {
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

    fun speedDensityIndependent(speedDensityIndependent: Boolean): PartyFactory {
        party = party.copy(speedDensityIndependent = speedDensityIndependent)
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
