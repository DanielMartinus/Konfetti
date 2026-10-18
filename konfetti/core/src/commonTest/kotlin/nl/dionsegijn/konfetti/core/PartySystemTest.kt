package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.CoreRect
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PartySystemTest {
    private val rect: CoreRect = CoreRectImpl(width = 1000f, height = 1000f)

    // Average between for each frame
    private val deltaTime = 0.017f

    @Test
    fun `Test creating particle every 25ms`() {
        val party =
            Party(
                emitter = Emitter(100L).max(4),
            )
        val system = PartySystem(party, pixelDensity = 1f)

        assertTrue(system.enabled)
        assertFalse(system.isDoneEmitting())

        val r1 = system.render(deltaTime, rect) // render 2, total deltaTime = 0.017f
        assertEquals(0, r1.size) // Expected 0, Every 0.025ms a new particle should be created

        val r2 = system.render(deltaTime, rect) // render 2, total deltaTime = 2 * 0.017f = 0.034f
        assertEquals(1, r2.size) // Expected 1, one for every 0.025ms

        val r3 = system.render(deltaTime, rect) // render 3, total deltaTime = 3 * 0.017f = 0.051f
        assertEquals(2, r3.size) // expected 2, one for every 0.025ms
    }

    @Test
    fun `Test PartySystem set to disabled stops generating particles`() {
        val party =
            Party(
                emitter = Emitter(100L).max(4),
            )
        val system = PartySystem(party, pixelDensity = 1f)

        assertTrue(system.enabled)
        assertFalse(system.isDoneEmitting())

        val r1 = system.render(deltaTime, rect) // render 2, total deltaTime = 0.017f
        assertEquals(0, r1.size) // Expected 0, Every 0.025ms a new particle should be created

        val r2 = system.render(deltaTime, rect) // render 2, total deltaTime = 2 * 0.017f = 0.034f
        assertEquals(1, r2.size) // Expected 1, one for every 0.025ms

        // System set to false, emitter will no longer asked for new particles
        system.enabled = false
        assertFalse(system.enabled)

        // Should not longer create new particles even though time has passed
        val r3 = system.render(deltaTime, rect)
        assertEquals(1, r3.size)
    }

    @Test
    fun `Test PartySystem is done Emitting`() {
        val party =
            Party(
                timeToLive = 1L,
                fadeOutEnabled = false,
                emitter = Emitter(100).max(2),
            )
        val system = PartySystem(party, pixelDensity = 1f)

        // Set drawArea to 1 pixel to let every particle directly disappear for this test
        rect.height = 1f
        assertTrue(system.enabled)

        system.render(deltaTime, rect) // dt: 0.017f
        system.render(deltaTime, rect) // dt: 0.034f
        system.render(deltaTime, rect) // dt: 0.051f
        system.render(deltaTime, rect) // dt: 0.068f
        system.render(deltaTime, rect) // dt: 0.085f

        // should still run because emitter isn't done yet, total delta time is < 100ms
        assertFalse(system.isDoneEmitting())

        system.render(deltaTime, rect) // dt: 0.102f // duration is higher than 100ms

        assertEquals(0, system.getActiveParticleAmount())
        assertTrue(system.isDoneEmitting())
    }

    @Test
    fun `Test PartySystem remove dead particles`() {
        // removes particles after two frames
        // Create particle every 20ms
        val party =
            Party(
                timeToLive = 18L,
                fadeOutEnabled = false,
                emitter = Emitter(100L).max(5),
            )
        val system = PartySystem(party, pixelDensity = 1f)

        // Every 20ms a new particle is created and every two frames they're removed

        system.render(deltaTime, rect) // dt: 0.017f
        system.render(deltaTime, rect) // dt: 0.034f
        assertEquals(1, system.getActiveParticleAmount())

        system.render(deltaTime, rect) // dt: 0.051f
        system.render(deltaTime, rect) // dt: 0.068f
        system.render(deltaTime, rect) // dt: 0.085f
        system.render(deltaTime, rect) // dt: 0.102f

        // All particles are created and one extra frame is executed to remove the last one
        system.render(deltaTime, rect) // dt: 0.119f
        assertEquals(0, system.getActiveParticleAmount())
    }

    @Test
    fun `Test PartySystem with the same seed produces identical particles`() {
        val drawArea = CoreRectImpl(0f, 0f, 1080f, 1920f)
        val party =
            Party(
                maxSpeed = 50f,
                emitter = Emitter(100L).max(20),
            )
        val first = PartySystem(party, pixelDensity = 1f, random = Random(42L))
        val second = PartySystem(party, pixelDensity = 1f, random = Random(42L))

        repeat(10) {
            assertEquals(first.render(deltaTime, drawArea), second.render(deltaTime, drawArea))
        }
        assertTrue(first.getActiveParticleAmount() > 0)
    }
}
