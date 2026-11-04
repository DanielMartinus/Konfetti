package nl.dionsegijn.samples.playground

import nl.dionsegijn.konfetti.core.Drift
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PartyCodeTest {
    @Test
    fun `a default party only prints its emitter`() {
        val code = partyCode(listOf(Party(emitter = Emitter(100L).max(100))))
        assertEquals("Party(\n    emitter = Emitter(100.milliseconds).max(100),\n)", code)
    }

    @Test
    fun `a timed perSecond emitter stays perSecond`() {
        val code = partyCode(listOf(Party(emitter = Emitter(5.seconds).perSecond(30))))
        assertTrue("emitter = Emitter(5.seconds).perSecond(30)" in code, code)
    }

    @Test
    fun `an endless emitter prints a zero duration`() {
        val code = partyCode(listOf(Party(emitter = Emitter(Duration.ZERO).perSecond(30))))
        assertTrue("emitter = Emitter(Duration.ZERO).perSecond(30)" in code, code)
    }

    @Test
    fun `drift is printed`() {
        val code = partyCode(listOf(Party(emitter = Emitter(100L).max(100), drift = Drift(enabled = true, sway = 40f))))
        assertTrue("drift = Drift(enabled = true, sway = 40f)" in code, code)
    }

    @Test
    fun `every rotation setting is printed`() {
        val rotation = Rotation(speed = 2f, multiplier3D = 0f, faceDirection = true)
        val code = partyCode(listOf(Party(emitter = Emitter(100L).max(100), rotation = rotation)))
        assertTrue("rotation = Rotation(speed = 2f, multiplier3D = 0f, faceDirection = true)" in code, code)
    }
}
