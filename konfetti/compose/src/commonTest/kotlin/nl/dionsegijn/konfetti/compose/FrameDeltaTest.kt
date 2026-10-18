package nl.dionsegijn.konfetti.compose

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class FrameDeltaTest {
    @Test
    fun `a normal frame gap is kept`() {
        assertEquals(16L, frameDeltaMillis(1_000L, 1_016L))
    }

    @Test
    fun `a missing or reset previous frame contributes nothing`() {
        assertEquals(0L, frameDeltaMillis(0L, 60_000L))
    }

    @Test
    fun `a background gap is clamped to one short frame`() {
        assertEquals(MAX_FRAME_DELTA_MILLIS, frameDeltaMillis(1_000L, 61_000L))
    }

    @Test
    fun `a capped background gap does not dump the party`() {
        val party =
            Party(
                emitter = Emitter(2000.milliseconds).perSecond(50),
            )
        val drawArea = CoreRectImpl(0f, 0f, 1000f, 1000f)
        val system = PartySystem(party, pixelDensity = 1f)

        // Emission has started, so this matches a pause in the middle of a party.
        system.render(0.016f, drawArea)

        val gapSeconds = frameDeltaMillis(1_000L, 61_000L) / 1000f
        system.render(gapSeconds, drawArea)

        assertTrue(system.getActiveParticleAmount() <= 5)
        assertFalse(system.isDoneEmitting())
    }
}
