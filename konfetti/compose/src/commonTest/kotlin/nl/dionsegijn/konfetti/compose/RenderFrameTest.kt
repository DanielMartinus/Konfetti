package nl.dionsegijn.konfetti.compose

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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
            emitter = Emitter(100.milliseconds).max(5),
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
                Party(emitter = Emitter(60.seconds).perSecond(10)),
                pixelDensity = 1f,
            )
        val systems = mutableListOf(short, long)
        val listener = RecordingListener()

        repeat(600) { renderFrame(systems, 0.016f, drawArea, listener) }

        assertEquals(listOf(long), systems)
        assertEquals(listOf(short to 1), listener.ended)
    }

    @Test
    fun `systems that end in one frame count down to the ones still running`() {
        val first = PartySystem(shortParty().copy(delay = 60_000), pixelDensity = 1f)
        val second = PartySystem(shortParty().copy(delay = 60_000), pixelDensity = 1f)
        val systems = mutableListOf(first, second)
        val listener = RecordingListener()

        first.enabled = false
        second.enabled = false
        renderFrame(systems, 0.016f, drawArea, listener)

        assertEquals(listOf(first to 1, second to 0), listener.ended)
    }

    @Test
    fun `the listener can reset the systems while they end`() {
        val systems =
            mutableListOf(
                PartySystem(Party(emitter = Emitter(60.seconds).perSecond(10)), pixelDensity = 1f),
                PartySystem(shortParty(), pixelDensity = 1f),
                PartySystem(Party(emitter = Emitter(60.seconds).perSecond(10)), pixelDensity = 1f),
            )
        val listener =
            object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(
                    system: PartySystem,
                    activeSystems: Int,
                ) = systems.clear()
            }

        repeat(600) { renderFrame(systems, 0.016f, drawArea, listener) }

        assertTrue(systems.isEmpty())
    }

    @Test
    fun `the listener can stop another system while one ends`() {
        val other = PartySystem(Party(emitter = Emitter(60.seconds).perSecond(10)), pixelDensity = 1f)
        val systems =
            mutableListOf(
                PartySystem(shortParty(), pixelDensity = 1f),
                PartySystem(Party(emitter = Emitter(60.seconds).perSecond(10)), pixelDensity = 1f),
                other,
            )
        val listener =
            object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(
                    system: PartySystem,
                    activeSystems: Int,
                ) {
                    systems.remove(other)
                }
            }

        repeat(600) { renderFrame(systems, 0.016f, drawArea, listener) }

        assertEquals(1, systems.size)
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

    @Test
    fun `a system stopped while waiting for its delay ends right away`() {
        val system = PartySystem(shortParty().copy(delay = 60_000), pixelDensity = 1f)
        val systems = mutableListOf(system)
        val listener = RecordingListener()

        system.enabled = false
        renderFrame(systems, 0.016f, drawArea, listener)

        assertTrue(systems.isEmpty())
        assertEquals(listOf(system to 0), listener.ended)
    }
}
