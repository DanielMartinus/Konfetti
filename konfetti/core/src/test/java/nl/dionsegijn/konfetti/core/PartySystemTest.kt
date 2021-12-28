package nl.dionsegijn.konfetti.core

import android.graphics.Rect
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class PartySystemTest {

    private val rect: Rect = Mockito.mock(Rect::class.java).apply {
        Mockito.`when`(height()).thenReturn(1000)
        Mockito.`when`(contains(anyInt(), anyInt())).thenReturn(true)
    }

    // Average between for each frame
    private val deltaTime = 0.017f

    @Test
    fun `Test creating particle every 25ms`() {
        val party = Party(
            emitter = Emitter(100L).max(4)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val r1 = system.render(deltaTime, rect) // render 2, total deltaTime = 0.017f
        Assert.assertEquals(0, r1.size) // Expected 0, Every 0.025ms a new particle should be created

        val r2 = system.render(deltaTime, rect) // render 2, total deltaTime = 2 * 0.017f = 0.034f
        Assert.assertEquals(1, r2.size) // Expected 1, one for every 0.025ms

        val r3 = system.render(deltaTime, rect) // render 3, total deltaTime = 3 * 0.017f = 0.051f
        Assert.assertEquals(2, r3.size) // expected 2, one for every 0.025ms
    }

    @Test
    fun `Test creating Particles with high initial deltaTime`() {
        val party = Party(
            emitter = Emitter(100L).max(2)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val r1 = system.render(60f, rect)
        Assert.assertEquals(2, r1.size)
    }

    @Test
    fun `Test PartySystem set to disabled stops generating particles`() {
        val party = Party(
            emitter = Emitter(100L).max(4)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val r1 = system.render(deltaTime, rect) // render 2, total deltaTime = 0.017f
        Assert.assertEquals(0, r1.size) // Expected 0, Every 0.025ms a new particle should be created

        val r2 = system.render(deltaTime, rect) // render 2, total deltaTime = 2 * 0.017f = 0.034f
        Assert.assertEquals(1, r2.size) // Expected 1, one for every 0.025ms

        system.enabled = false
        Assert.assertFalse(system.enabled)

        // Should not longer create new particles even though time has passed
        val r3 = system.render(deltaTime, rect)
        Assert.assertEquals(1, r3.size)
    }
    }
}
