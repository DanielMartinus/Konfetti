package nl.dionsegijn.konfetti_core.emitter

import android.graphics.Rect
import nl.dionsegijn.konfetti_core.NewEmitter.Emitter
import nl.dionsegijn.konfetti_core.Party

/**
 * An abstract class for creating a custom emitter
 * The emitter decides if a particle should be created and when the emitter is finished
 */
abstract class BaseEmitter {

    /**
     * This function is called on each update when the [RenderSystem] is active
     * Keep this function as light as possible otherwise you'll slow down the render system
     */
    abstract fun createConfetti(deltaTime: Float, party: Party, drawArea: Rect): List<Confetti>

    /**
     * @return true if the emitter is no longer creating any particles
     *         false if is still busy
     */
    abstract fun isFinished(): Boolean

    companion object {
        fun burst(amount: Int) = Emitter(duration = 100L).max(amount)

        fun streamParticlesPerSecond(particlesPerSecond: Int, durationInMillis: Long) =
            Emitter(duration = durationInMillis).perSecond(particlesPerSecond)

        fun streamMaxParticles(maxParticles: Int, durationInMillis: Long) =
            Emitter(durationInMillis).max(maxParticles)
    }
}
