package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.models.CoreRect
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PartyEmitterTest {
    private val drawArea: CoreRect = CoreRectImpl(width = 1000f, height = 1000f)

    // Average time between for each frame
    private val deltaTime = 0.017f

    // Test Party object
    // Create confetti every 10ms
    private val party =
        Party(
            angle = Angle.TOP,
            spread = 0,
            speed = 30f,
            maxSpeed = -1f,
            damping = 0.9f,
            size = listOf(Size(sizeInDp = 6, mass = 5f, massVariance = 0f)),
            colors = listOf(0xFF0000),
            shapes = listOf(Shape.Square),
            timeToLive = 1000L,
            fadeOutEnabled = false,
            position = Position.Absolute(100f, 100f),
            delay = 0,
            rotation = Rotation(),
            emitter = Emitter(100L).max(10),
        )

    @Test
    fun `Create confetti every 25ms and then finish`() {
        // Create confetti every 25ms
        val party =
            Party(
                emitter = Emitter(100L).max(4),
            )
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 = emitter.createConfetti(deltaTime, party, drawArea) // 0.017f
        assertEquals(0, r1.size)

        val r2 = emitter.createConfetti(deltaTime, party, drawArea) // 0.034f
        assertEquals(1, r2.size)

        val r3 = emitter.createConfetti(deltaTime, party, drawArea) // 0.051f
        assertEquals(1, r3.size)

        val r4 = emitter.createConfetti(deltaTime, party, drawArea) // 0.068f
        assertEquals(0, r4.size)

        val r5 = emitter.createConfetti(deltaTime, party, drawArea) // 0.085f
        assertEquals(1, r5.size)
        assertFalse(emitter.isFinished())

        val r6 = emitter.createConfetti(deltaTime, party, drawArea) // 0.102f
        assertEquals(1, r6.size)
        assertTrue(emitter.isFinished())
    }

    @Test
    fun `Create confetti and check its initial state`() {
        val emitter = PartyEmitter(party.emitter, 1f, Random(1L))

        val r1 = emitter.createConfetti(deltaTime, party, drawArea) // 0.017f
        with(r1.first()) {
            assertEquals(Vector(100f, 100f), location)
            assertEquals(6f, width)
            assertEquals(Shape.Square, shape)
            assertEquals(1000L, lifespan)
            assertEquals(0.9f, damping)
            assertEquals(10.99149f, rotationSpeed2D, 0.0001f)
            assertEquals(1.1494478f, rotationSpeed3D, 0.0001f)
        }
    }

    @Test
    fun `Initial state confetti with rotation disabled`() {
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 =
            emitter.createConfetti(
                deltaTime,
                party.copy(rotation = Rotation.disabled()),
                drawArea,
            ) // 0.017f

        with(r1.first()) {
            assertEquals(0.0f, rotationSpeed2D)
            assertEquals(0.0f, rotationSpeed3D)
        }
    }

    @Test
    fun `Initial state confetti with relative position`() {
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 =
            emitter.createConfetti(
                deltaTime,
                party.copy(position = Position.Relative(0.5, 0.5)),
                drawArea,
            ) // 0.017f

        with(r1.first()) {
            assertEquals(Vector(500f, 500f), location)
        }
    }

    @Test
    fun `max keeps the same interval when the duration divides evenly`() {
        val config = Emitter(100L).max(4)

        assertEquals(0.025f, config.amountPerMs)
        assertEquals(4, config.maxParticles)
    }

    @Test
    fun `max does not spawn more particles than the cap on a long frame`() {
        val party = Party(emitter = Emitter(1000L).max(10))
        val emitter = PartyEmitter(party.emitter, 1f)

        assertEquals(0, emitter.createConfetti(deltaTime, party, drawArea).size)

        val burst = emitter.createConfetti(5f, party, drawArea)
        assertEquals(10, burst.size)
    }

    @Test
    fun `max with more particles than milliseconds does not divide to zero`() {
        val config = Emitter(50L).max(5_000)
        assertTrue(config.amountPerMs > 0f)

        val party = Party(emitter = config)
        val emitter = PartyEmitter(party.emitter, 1f)
        val created = emitter.createConfetti(1f, party, drawArea)

        assertTrue(created.size in 1..5_000)
    }
}
