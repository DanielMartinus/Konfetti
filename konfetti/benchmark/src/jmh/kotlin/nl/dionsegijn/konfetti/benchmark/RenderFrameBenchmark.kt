package nl.dionsegijn.konfetti.benchmark

import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.CoreRect
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * Cost of a single [PartySystem.render] call with a fixed number of active particles.
 *
 * Damping is 0 and the time to live is effectively infinite, so particles stay in place and the
 * particle count stays constant across invocations. Every call therefore does the same amount
 * of work while still running the full per-particle update.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
open class RenderFrameBenchmark {
    @Param("100", "1000", "10000")
    @JvmField
    var particles: Int = 0

    private lateinit var system: PartySystem
    private lateinit var drawArea: CoreRect

    @Setup(Level.Trial)
    fun setup() {
        drawArea = drawArea()
        val party =
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0f,
                spread = 360,
                timeToLive = Long.MAX_VALUE / 2,
                emitter = Emitter(duration = 100).perSecond(particles * 10),
            )
        system = PartySystem(party, pixelDensity = PIXEL_DENSITY, random = Random(SEED))

        // Run past the 100ms emitting window so no new particles are created during measurement
        repeat(EMITTING_FRAMES) { system.render(DELTA_TIME, drawArea) }
        check(system.getActiveParticleAmount() >= particles) {
            "Expected $particles particles, got ${system.getActiveParticleAmount()}"
        }
    }

    @Benchmark
    fun renderFrame(): List<Particle> = system.render(DELTA_TIME, drawArea)

    private companion object {
        const val EMITTING_FRAMES = 10
    }
}
