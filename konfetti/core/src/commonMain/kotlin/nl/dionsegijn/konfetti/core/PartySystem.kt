package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.emitter.BaseEmitter
import nl.dionsegijn.konfetti.core.emitter.Confetti
import nl.dionsegijn.konfetti.core.emitter.PartyEmitter
import nl.dionsegijn.konfetti.core.models.CoreRect
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Clock

/**
 * PartySystem is responsible for requesting particles from the emitter and updating the particles
 * everytime a new frame is requested.
 * @param party configuration class with instructions on how to create the particles for the Emitter
 * @param createdAt timestamp of when the partySystem is created
 * @param pixelDensity default value taken from resources to measure based on pixelDensity
 * @param random source of randomness for the emitter, pass a seeded instance for reproducible output
 */
class PartySystem(
    val party: Party,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    pixelDensity: Float,
    random: Random,
) {
    constructor(
        party: Party,
        createdAt: Long = Clock.System.now().toEpochMilliseconds(),
        pixelDensity: Float,
    ) : this(party, createdAt, pixelDensity, Random.Default)

    var enabled = true

    private var emitter: BaseEmitter = PartyEmitter(party.emitter, pixelDensity, random)

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

        // Every piece of a party shares its damping, so work out this frame's step once
        val steps = deltaTime * Confetti.STEPS_PER_SECOND
        val stepDamping = party.damping.pow(steps)
        activeParticles.forEach {
            // A custom emitter may give pieces their own damping
            if (it.damping == party.damping) {
                it.render(deltaTime, drawArea, steps, stepDamping)
            } else {
                it.render(deltaTime, drawArea)
            }
        }

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
    val ribbon = ribbonBody()
    return Particle(
        location.x,
        location.y,
        width,
        ribbon?.length ?: width,
        alphaColor,
        rotation + tiltAngle,
        scaleX,
        shape,
        alpha,
        ribbon,
    )
}
