package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.emitter.BaseEmitter
import nl.dionsegijn.konfetti.core.emitter.Confetti
import nl.dionsegijn.konfetti.core.emitter.PartyEmitter
import nl.dionsegijn.konfetti.core.models.CoreRect

/**
 * PartySystem is responsible for requesting particles from the emitter and updating the particles
 * everytime a new frame is requested.
 * @param party configuration class with instructions on how to create the particles for the Emitter
 * @param createdAt timestamp of when the partySystem is created
 * @param pixelDensity default value taken from resources to measure based on pixelDensity
 */
class PartySystem(
    val party: Party,
    val createdAt: Long = System.currentTimeMillis(),
    pixelDensity: Float,
) {
    var enabled = true

    private var emitter: BaseEmitter = PartyEmitter(party.emitter, pixelDensity)

    private val activeParticles = mutableListOf<Confetti>()

    // Called every frame to create and update the particles state
    // returns a list of particles that are ready to be rendered
    fun render(
        deltaTime: Float,
        drawArea: CoreRect,
    ): List<Particle> {
        if (enabled) {
            activeParticles.addAll(emitter.createConfetti(deltaTime, party, drawArea))
        }

        activeParticles.forEach { it.render(deltaTime, drawArea) }

        activeParticles.removeAll { it.isDead() }

        return activeParticles.filter { it.drawParticle }.map { it.toParticle() }
    }

    /**
     * When the emitter is done emitting.
     * @return true if the emitter is done emitting or false when it's still busy or needs to start
     * based on the delay
     */
    fun isDoneEmitting(): Boolean = (emitter.isFinished() && activeParticles.size == 0) || (!enabled && activeParticles.size == 0)

    fun getActiveParticleAmount() = activeParticles.size
}

/**
 * Convert a confetti object to a particle object with instructions on how to draw
 * the confetti to a canvas
 */
fun Confetti.toParticle(): Particle {
    return Particle(
        location.x,
        location.y,
        width,
        width,
        alphaColor,
        rotation,
        scaleX,
        shape,
        alpha,
    )
}
