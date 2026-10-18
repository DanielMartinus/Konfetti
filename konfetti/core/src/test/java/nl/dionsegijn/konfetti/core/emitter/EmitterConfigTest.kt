package nl.dionsegijn.konfetti.core.emitter

import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class EmitterConfigTest {
    @Test
    fun `a Duration sets the emitting time in milliseconds`() {
        Assert.assertEquals(2000L, Emitter(2.seconds).max(10).emittingTime)
    }

    @Test
    fun `a TimeUnit still converts to milliseconds`() {
        Assert.assertEquals(2000L, Emitter(2, TimeUnit.SECONDS).max(10).emittingTime)
    }
}
