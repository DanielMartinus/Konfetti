package nl.dionsegijn.konfetti.benchmark

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
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
import org.openjdk.jmh.infra.Blackhole
import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * Cost of running a preset from the first frame until every [PartySystem] is done emitting and
 * all particles are gone, the same way KonfettiView drives it.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class LifecycleBenchmark {
    @Param("explode", "rain", "festive")
    @JvmField
    var preset: String = ""

    private lateinit var parties: List<Party>
    private lateinit var drawArea: CoreRect

    @Setup(Level.Trial)
    fun setup() {
        parties = Presets.get(preset)
        drawArea = drawArea()
    }

    @Benchmark
    fun fullLifecycle(blackhole: Blackhole): Int {
        val random = Random(SEED)
        val systems = parties.map { PartySystem(it, createdAt = 0L, pixelDensity = PIXEL_DENSITY, random = random) }

        var frames = 0
        while (systems.any { !it.isDoneEmitting() }) {
            check(frames++ < MAX_FRAMES) { "Preset $preset did not finish within $MAX_FRAMES frames" }
            systems.forEach { blackhole.consume(it.render(DELTA_TIME, drawArea)) }
        }
        return frames
    }

    private companion object {
        /** 60 seconds at 60fps, far beyond the longest preset */
        const val MAX_FRAMES = 3_600
    }
}
