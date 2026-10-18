package nl.dionsegijn.samples.playground

import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.models.CoreRect
import kotlin.random.Random

/**
 * Plays parties from the start with a fixed step and seeded randomness, so the same time always
 * shows the same confetti. That's what lets the timeline scrub back and forth.
 * Each party plays in its own [Lane], so an edit only has to replay the lane that changed.
 */
class Replay(
    val lanes: List<Lane>,
) {
    constructor(parties: List<Party>, drawArea: CoreRect, pixelDensity: Float) :
        this(parties.mapIndexed { i, party -> Lane(party, drawArea, pixelDensity, SEED + i) })

    /** Particles to draw at [timeMs] after the start */
    fun particlesAt(timeMs: Long): List<Particle> = lanes.flatMap { it.particlesAt(timeMs) }

    /** How many confetti are on screen at each step until [timeMs], for the curve under the lanes */
    fun particleCounts(timeMs: Long): List<Int> {
        val perLane = lanes.map { it.particleCounts(timeMs) }
        return List((timeMs / STEP_MS).toInt()) { step -> perLane.sumOf { it[step] } }
    }

    /**
     * A replay for [parties] that keeps the lanes that didn't change. New lanes are played up to
     * [timeMs] right away, so this can run off the main thread while the kept lanes keep playing.
     */
    fun edit(
        parties: List<Party>,
        drawArea: CoreRect,
        pixelDensity: Float,
        timeMs: Long,
    ): Replay =
        Replay(
            parties.mapIndexed { i, party ->
                lanes.getOrNull(i)?.takeIf { it.party == party && it.drawArea == drawArea && it.pixelDensity == pixelDensity }
                    ?: Lane(party, drawArea, pixelDensity, SEED + i).also { it.particlesAt(timeMs) }
            },
        )

    companion object {
        // One frame at 60 fps
        const val STEP_MS = 16L

        // Each party gets its own seed, so changing one lane doesn't reshuffle the others
        private const val SEED = 42L
    }
}

/** One party on the timeline, played on its own */
class Lane(
    val party: Party,
    val drawArea: CoreRect,
    val pixelDensity: Float,
    private val seed: Long,
) {
    private var system = newSystem()
    private var elapsedMs = 0L
    private var particles = emptyList<Particle>()

    // Kept for when another lane changes
    private var counts = emptyList<Int>()

    /** Particles to draw at [timeMs] after the start */
    fun particlesAt(timeMs: Long): List<Particle> {
        // The simulation only runs forward, so going back means starting over
        if (timeMs < elapsedMs) reset()
        while (elapsedMs + Replay.STEP_MS <= timeMs) step()
        return particles
    }

    /** How many confetti are on screen at each step until [timeMs] */
    fun particleCounts(timeMs: Long): List<Int> {
        val steps = (timeMs / Replay.STEP_MS).toInt()
        if (counts.size == steps) return counts
        // Counts on a copy, so the confetti on screen don't move
        val copy = Lane(party, drawArea, pixelDensity, seed)
        counts = List(steps) { copy.particlesAt((it + 1) * Replay.STEP_MS).size }
        return counts
    }

    private fun step() {
        elapsedMs += Replay.STEP_MS
        // A party waits for its delay, like it does in KonfettiView
        if (elapsedMs >= party.delay) particles = system.render(Replay.STEP_MS / 1000f, drawArea)
    }

    private fun reset() {
        system = newSystem()
        elapsedMs = 0L
        particles = emptyList()
    }

    private fun newSystem() = PartySystem(party, createdAt = 0L, pixelDensity = pixelDensity, random = Random(seed))
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
