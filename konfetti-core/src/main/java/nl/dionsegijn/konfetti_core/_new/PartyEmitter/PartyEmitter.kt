package nl.dionsegijn.konfetti_core._new.PartyEmitter

import nl.dionsegijn.konfetti_core.Confetti
import nl.dionsegijn.konfetti_core._new.NewEmitter.EmitterBase
import nl.dionsegijn.konfetti_core._new.Party
import nl.dionsegijn.konfetti_core.emitters.RenderSystem

/**
 * An abstract class for creating a custom emitter
 * The emitter decides if a particle should be created and when the emitter is finished
 */
abstract class PartyEmitter {

    /**
     * This function is called on each update when the [RenderSystem] is active
     * Keep this function as light as possible otherwise you'll slow down the render system
     */
    abstract fun createConfetti(deltaTime: Float, party: Party): List<Confetti>

    /**
     * @return true if the emitter is no longer creating any particles
     *         false if is still busy
     */
    abstract fun isFinished(): Boolean

    companion object {
        fun burst(amount: Int) = EmitterBase(duration = 100L).max(amount)

        fun streamParticlesPerSecond(particlesPerSecond: Int, durationInMillis: Long) =
            EmitterBase(duration = durationInMillis).perSecond(particlesPerSecond)

        fun streamMaxParticles(maxParticles: Int, durationInMillis: Long) =
            EmitterBase(durationInMillis).max(maxParticles)
    }
}
