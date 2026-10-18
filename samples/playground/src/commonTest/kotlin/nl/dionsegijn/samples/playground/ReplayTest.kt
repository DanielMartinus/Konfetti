package nl.dionsegijn.samples.playground

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class ReplayTest {
    private val drawArea = CoreRectImpl(0f, 0f, 1000f, 1000f)
    private val explode = Party(emitter = Emitter(100.milliseconds).max(50))

    private fun replay(vararg parties: Party) = Replay(parties.toList(), drawArea, pixelDensity = 1f)

    @Test
    fun `the same time shows the same confetti`() {
        assertEquals(replay(explode).particlesAt(800), replay(explode).particlesAt(800))
    }

    @Test
    fun `scrubbing back shows what playing straight to that time shows`() {
        val scrubbed = replay(explode)
        scrubbed.particlesAt(1500)

        assertEquals(replay(explode).particlesAt(500), scrubbed.particlesAt(500))
    }

    @Test
    fun `a party waits for its delay`() {
        val replay = replay(explode.copy(delay = 1000))

        assertTrue(replay.particlesAt(900).isEmpty())
        assertTrue(replay.particlesAt(1100).isNotEmpty())
    }

    @Test
    fun `a lane ends after emitting and the last confetti fading out`() {
        val party = explode.copy(delay = 200, timeToLive = 1000)

        assertEquals(200L + 100L + 1000L + 850L, party.endMs())
    }

    @Test
    fun `an edit keeps the lanes that didn't change`() {
        val before = replay(explode, explode.copy(angle = 0))
        val after = before.edit(listOf(explode, explode.copy(angle = 180)), drawArea, pixelDensity = 1f, timeMs = 0)

        assertSame(before.lanes[0], after.lanes[0])
        assertNotSame(before.lanes[1], after.lanes[1])
    }

    @Test
    fun `an edited lane shows what playing it from the start shows`() {
        val edited = explode.copy(angle = 180)
        val after = replay(explode, explode).edit(listOf(explode, edited), drawArea, pixelDensity = 1f, timeMs = 500)

        assertEquals(replay(explode, edited).particlesAt(800), after.particlesAt(800))
    }

    @Test
    fun `counting doesn't move the confetti on screen`() {
        val counted = replay(explode)
        counted.particlesAt(500)
        counted.particleCounts(2000)

        assertEquals(replay(explode).particlesAt(500), counted.particlesAt(500))
    }
}
