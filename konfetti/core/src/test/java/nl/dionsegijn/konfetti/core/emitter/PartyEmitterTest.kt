package nl.dionsegijn.konfetti.core.emitter

import android.graphics.Rect
import nl.dionsegijn.konfetti.core.Party
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class PartyEmitterTest {

    private val drawArea: Rect = Mockito.mock(Rect::class.java).apply {
        Mockito.`when`(height()).thenReturn(1000)
        Mockito.`when`(contains(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(true)
    }

    // Average time between for each frame
    private val deltaTime = 0.017f

    @Test
    fun `Create confetti every 25ms and then finish`() {
        val party = Party(
            emitter = Emitter(100L).max(4) // Create confetti every 25ms
        )
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 = emitter.createConfetti(deltaTime, party, drawArea) // 0.017f
        Assert.assertEquals(0, r1.size)

        val r2 = emitter.createConfetti(deltaTime, party, drawArea) // 0.034f
        Assert.assertEquals(1, r2.size)

        val r3 = emitter.createConfetti(deltaTime, party, drawArea) // 0.051f
        Assert.assertEquals(1, r3.size)

        val r4 = emitter.createConfetti(deltaTime, party, drawArea) // 0.068f
        Assert.assertEquals(0, r4.size)

        val r5 = emitter.createConfetti(deltaTime, party, drawArea) // 0.085f
        Assert.assertEquals(1, r5.size)
        Assert.assertFalse(emitter.isFinished())

        val r6 = emitter.createConfetti(deltaTime, party, drawArea) // 0.102f
        Assert.assertEquals(1, r6.size)
        Assert.assertTrue(emitter.isFinished())
    }

    @Test
    fun `Create confetti every 25ms and then finish`() {
        val party = Party(
            emitter = Emitter(100L).perSecond(1)
        )
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 = emitter.createConfetti(deltaTime, party, drawArea) // 0.017f
        Assert.assertEquals(0, r1.size)

        val r2 = emitter.createConfetti(deltaTime, party, drawArea) // 0.034f
        Assert.assertEquals(1, r2.size)

        val r3 = emitter.createConfetti(deltaTime, party, drawArea) // 0.051f
        Assert.assertEquals(1, r3.size)

        val r4 = emitter.createConfetti(deltaTime, party, drawArea) // 0.068f
        Assert.assertEquals(0, r4.size)

        val r5 = emitter.createConfetti(deltaTime, party, drawArea) // 0.085f
        Assert.assertEquals(1, r5.size)
        Assert.assertFalse(emitter.isFinished())

        val r6 = emitter.createConfetti(deltaTime, party, drawArea) // 0.102f
        Assert.assertEquals(1, r6.size)
        Assert.assertTrue(emitter.isFinished())
    }
}
