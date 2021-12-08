package nl.dionsegijn.konfetti_core

import android.graphics.Rect
import nl.dionsegijn.konfetti_core.emitter.BaseEmitter
import nl.dionsegijn.konfetti_core.emitter.Confetti
import nl.dionsegijn.konfetti_core.emitter.PartyEmitter
import nl.dionsegijn.konfetti_core.models.Vector

class PartySystem(
    val party: Party,
    val createdAt: Long = System.currentTimeMillis()
) {

    private var enabled = true

    private var gravity = Vector(0f, 0.01f)

    private var emitter: BaseEmitter = PartyEmitter(party.emitter)

    private val activeParticles = mutableListOf<Confetti>()

    // Called every frame to create and update the particles state
    // returns a list of particles that are ready to be rendered
    fun render(deltaTime: Float, drawArea: Rect): List<Particle> {
        activeParticles.addAll(emitter.createConfetti(deltaTime, party))

        for (i in activeParticles.size - 1 downTo 0) {
            val particle = activeParticles[i]
            particle.applyForce(gravity)
            particle.render(deltaTime, drawArea)
        }

        activeParticles.removeAll { it.isDead() }

        return activeParticles.filter { it.drawParticle }.map { it.toParticle() }
    }

    fun isDoneEmitting(): Boolean =
        (emitter.isFinished() && activeParticles.size == 0) || (!enabled && activeParticles.size == 0)

}

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
        alpha
    )
}
