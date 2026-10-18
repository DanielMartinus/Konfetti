package nl.dionsegijn.samples.playground

import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.models.CoreRect
import kotlin.random.Random

/**
 * Plays parties from the start with a fixed step and seeded randomness, so the same time always
 * shows the same confetti. That's what lets the timeline scrub back and forth.
 */
class Replay(
    private val parties: List<Party>,
    private val drawArea: CoreRect,
    private val pixelDensity: Float,
    private val seed: Long = 42L,
) {
    private var systems = newSystems()
    private var elapsedMs = 0L
    private var particles = emptyList<Particle>()

    /** Particles to draw at [timeMs] after the start */
    fun particlesAt(timeMs: Long): List<Particle> {
        // The simulation only runs forward, so going back means starting over
        if (timeMs < elapsedMs) reset()
        while (elapsedMs + STEP_MS <= timeMs) step()
        return particles
    }

    /** How many confetti are on screen at each step until [timeMs], for the curve under the lanes */
    fun particleCounts(timeMs: Long): List<Int> {
        reset()
        val counts = mutableListOf<Int>()
        while (elapsedMs + STEP_MS <= timeMs) {
            step()
            counts += particles.size
        }
        reset()
        return counts
    }

    private fun step() {
        elapsedMs += STEP_MS
        particles =
            systems.flatMapIndexed { i, system ->
                // A party waits for its delay, like it does in KonfettiView
                if (elapsedMs < parties[i].delay) emptyList() else system.render(STEP_MS / 1000f, drawArea)
            }
    }

    private fun reset() {
        systems = newSystems()
        elapsedMs = 0L
        particles = emptyList()
    }

    // Each party gets its own seed, so changing one lane doesn't reshuffle the others
    private fun newSystems() =
        parties.mapIndexed { i, party ->
            PartySystem(party, createdAt = 0L, pixelDensity = pixelDensity, random = Random(seed + i))
        }

    companion object {
        // One frame at 60 fps
        const val STEP_MS = 16L
    }
}

// Time it takes the last confetti to fade out, 5 alpha per frame at 60 fps
private const val FADE_MS = 850L

// Emitters without a duration run forever, show them as this long on the timeline
private const val ENDLESS_MS = 5_000L

/** Where a party starts on the timeline, in milliseconds */
fun Party.startMs(): Long = delay.toLong()

/** Where a party ends on the timeline: after its delay, emitting and the last confetti fading out */
fun Party.endMs(): Long {
    val emitting = emitter.emittingTime.takeIf { it > 0 } ?: ENDLESS_MS
    return startMs() + emitting + timeToLive + FADE_MS
}
