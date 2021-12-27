package nl.dionsegijn.konfetti.core

import android.graphics.Rect
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class PartySystemTest {

    @Test
    fun `Test creating particles every 50ms per frame`() {
        val party = Party(
            emitter = Emitter(100L).max(2)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val rect = Mockito.mock(Rect::class.java)
        Mockito.`when`(rect.height()).thenReturn(100)
        Mockito.`when`(rect.contains(anyInt(), anyInt())).thenReturn(true)

        val render1 = system.render(0.05f, rect)
        Assert.assertEquals(1, render1.size)

        val render2 = system.render(0.06f, rect)
        Assert.assertEquals(2, render2.size)
    }

    @Test
    fun `Test creating Particles with high initial deltaTime`() {
        val party = Party(
            emitter = Emitter(100L).max(2)
        )
        val system = PartySystem(party, pixelDensity = 1f)

        Assert.assertTrue(system.enabled)
        Assert.assertFalse(system.isDoneEmitting())

        val rect = Mockito.mock(Rect::class.java)
        Mockito.`when`(rect.height()).thenReturn(100)
        Mockito.`when`(rect.contains(anyInt(), anyInt())).thenReturn(true)

        val particles = system.render(60f, rect)
        Assert.assertEquals(2, particles.size)
    }
}
