package nl.dionsegijn.konfetti.xml

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerIntegrationTest {
    @Test
    fun `the first frame contributes next to nothing`() {
        assertEquals(0f, KonfettiView.TimerIntegration().getDeltaTime(), 0.001f)
    }

    @Test
    fun `a normal frame gap is kept`() {
        val timer = KonfettiView.TimerIntegration()
        timer.getDeltaTime()
        Thread.sleep(20)
        val delta = timer.getDeltaTime()
        assertTrue("delta was $delta", delta in 0.015f..0.1f)
    }

    @Test
    fun `a long pause is capped to one short frame`() {
        val timer = KonfettiView.TimerIntegration()
        timer.getDeltaTime()
        Thread.sleep(300)
        assertEquals(0.1f, timer.getDeltaTime())
    }
}
