package nl.dionsegijn.konfetti.compose

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class RenderFrameTest {
    private val drawArea = CoreRectImpl(0f, 0f, 1000f, 1000f)

    private class RecordingListener : OnParticleSystemUpdateListener {
        val ended = mutableListOf<Pair<PartySystem, Int>>()

        override fun onParticleSystemEnded(
            system: PartySystem,
            activeSystems: Int,
        ) {
            ended.add(system to activeSystems)
        }
    }

    private fun shortParty() =
        Party(
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(5),
            timeToLive = 100L,
        )

    @Test
    fun `a finished system is reported once and removed`() {
        val system = PartySystem(shortParty(), pixelDensity = 1f)
        val systems = mutableListOf(system)
        val listener = RecordingListener()

        // 10 seconds of frames, far longer than the party lasts
        repeat(600) { renderFrame(systems, 0.016f, drawArea, listener) }

        assertTrue(systems.isEmpty())
        assertEquals(listOf(system to 0), listener.ended)
    }

    @Test
    fun `the end callback counts the systems that are still running`() {
        val short = PartySystem(shortParty(), pixelDensity = 1f)
        val long =
            PartySystem(
                Party(emitter = Emitter(duration = 60, TimeUnit.SECONDS).perSecond(10)),
                pixelDensity = 1f,
            )
        val systems = mutableListOf(short, long)
        val listener = RecordingListener()

        repeat(600) { renderFrame(systems, 0.016f, drawArea, listener) }

        assertEquals(listOf(long), systems)
        assertEquals(listOf(short to 1), listener.ended)
    }

    @Test
    fun `a system waiting for its delay draws nothing and stays`() {
        val system = PartySystem(shortParty().copy(delay = 60_000), pixelDensity = 1f)
        val systems = mutableListOf(system)
        val listener = RecordingListener()

        val particles = renderFrame(systems, 0.016f, drawArea, listener)

        assertTrue(particles.isEmpty())
        assertEquals(listOf(system), systems)
        assertTrue(listener.ended.isEmpty())
    }
}
