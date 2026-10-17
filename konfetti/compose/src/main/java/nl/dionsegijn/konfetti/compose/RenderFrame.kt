package nl.dionsegijn.konfetti.compose

import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.models.CoreRect

/**
 * Renders one frame of every system and returns the particles to draw.
 *
 * A system that is done is removed from [systems] and reported once through [updateListener],
 * with the number of systems that are still running. A system that is still waiting for its
 * delay stays in the list but draws nothing.
 */
internal fun renderFrame(
    systems: MutableList<PartySystem>,
    deltaSeconds: Float,
    drawArea: CoreRect,
    updateListener: OnParticleSystemUpdateListener?,
): List<Particle> {
    val particles = mutableListOf<Particle>()
    val iterator = systems.iterator()
    while (iterator.hasNext()) {
        val system = iterator.next()
        // Do not start the system yet if it is still waiting for its delay
        if (getTotalTimeRunning(system.createdAt) < system.party.delay) continue

        particles.addAll(system.render(deltaSeconds, drawArea))

        if (system.isDoneEmitting()) {
            iterator.remove()
            updateListener?.onParticleSystemEnded(system = system, activeSystems = systems.size)
        }
    }
    return particles
}
