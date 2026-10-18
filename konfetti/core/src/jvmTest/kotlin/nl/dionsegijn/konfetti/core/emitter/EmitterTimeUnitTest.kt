package nl.dionsegijn.konfetti.core.emitter

import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class EmitterTimeUnitTest {
    @Test
    fun `a TimeUnit still converts to milliseconds`() {
        assertEquals(2000L, Emitter(2, TimeUnit.SECONDS).max(10).emittingTime)
    }
}
