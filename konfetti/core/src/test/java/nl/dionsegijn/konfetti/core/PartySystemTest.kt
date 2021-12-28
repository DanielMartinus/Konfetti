package nl.dionsegijn.konfetti.core

import android.graphics.Rect
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class PartySystemTest {

    private val rect: Rect = Mockito.mock(Rect::class.java).apply {
        Mockito.`when`(height()).thenReturn(100)
        Mockito.`when`(contains(anyInt(), anyInt())).thenReturn(true)
    }

    @Test
    fun `Test creating particle after 50ms`() {
        val party = Party(
            emitter = Emitter(100L).max(2)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val render1 = system.render(0.05f, rect)
        Assert.assertEquals(1, render1.size)
    }

    @Test
    fun `Test creating Particles with high initial deltaTime`() {
        val party = Party(
            emitter = Emitter(100L).max(2)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val particles = system.render(60f, rect)
        Assert.assertEquals(2, particles.size)
    }
}
