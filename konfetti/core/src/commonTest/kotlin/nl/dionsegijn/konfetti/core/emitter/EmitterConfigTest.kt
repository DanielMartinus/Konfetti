package nl.dionsegijn.konfetti.core.emitter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class EmitterConfigTest {
    @Test
    fun `a Duration sets the emitting time in milliseconds`() {
        assertEquals(2000L, Emitter(2.seconds).max(10).emittingTime)
    }

    @Test
    fun `a DurationUnit sets the same emitter as a Duration`() {
        assertEquals(Emitter(5000.milliseconds).max(10), Emitter(5, DurationUnit.SECONDS).max(10))
    }
}
