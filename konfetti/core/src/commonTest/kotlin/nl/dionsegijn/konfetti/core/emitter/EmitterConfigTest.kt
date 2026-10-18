package nl.dionsegijn.konfetti.core.emitter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class EmitterConfigTest {
    @Test
    fun `a Duration sets the emitting time in milliseconds`() {
        assertEquals(2000L, Emitter(2.seconds).max(10).emittingTime)
    }
}
