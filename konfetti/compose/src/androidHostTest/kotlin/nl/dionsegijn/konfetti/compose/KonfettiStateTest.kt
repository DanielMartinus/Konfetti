package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.geometry.Offset
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KonfettiStateTest {
    private val explode = Party(emitter = Emitter(100L).max(10))
    private val rain = Party(emitter = Emitter(5000L).perSecond(30))

    @Test
    fun `a started party runs from the next frame`() {
        val state = KonfettiState()
        state.start(explode)
        assertTrue(state.isRunning())
        assertTrue(state.systems.isEmpty())

        state.startPending(pixelDensity = 2f)

        assertEquals(1, state.systems.size)
        assertTrue(state.pending.isEmpty())
    }

    @Test
    fun `starting again keeps the running parties`() {
        val state = KonfettiState()
        state.start(explode)
        state.startPending(pixelDensity = 1f)

        state.start(rain)
        state.startPending(pixelDensity = 1f)

        assertEquals(listOf(explode, rain), state.systems.map { it.party })
    }

    @Test
    fun `burst starts the party at the given position`() {
        val state = KonfettiState()
        state.burst(at = Offset(120f, 300f), party = explode)

        assertEquals(Position.Absolute(120f, 300f), state.pending.single().position)
    }

    @Test
    fun `stop removes only that party`() {
        val state = KonfettiState()
        state.start(explode, rain)
        state.startPending(pixelDensity = 1f)

        state.stop(explode)

        assertEquals(listOf(rain), state.systems.map { it.party })
    }

    @Test
    fun `stopGracefully lets running parties finish but starts nothing new`() {
        val state = KonfettiState()
        state.start(explode)
        state.startPending(pixelDensity = 1f)
        state.start(rain)

        state.stopGracefully()

        assertTrue(state.pending.isEmpty())
        assertFalse(state.systems.single().enabled)
    }

    @Test
    fun `reset stops everything`() {
        val state = KonfettiState()
        state.start(explode)
        state.startPending(pixelDensity = 1f)
        state.start(rain)

        state.reset()

        assertFalse(state.isRunning())
    }
}
