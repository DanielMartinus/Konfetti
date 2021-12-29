package nl.dionsegijn.konfetti.core.emitter

import android.graphics.Color
import android.graphics.Rect
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.core.models.Vector
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.Random

class PartyEmitterTest {

    private val drawArea: Rect = Mockito.mock(Rect::class.java).apply {
        Mockito.`when`(height()).thenReturn(1000)
        Mockito.`when`(width()).thenReturn(1000)
        Mockito.`when`(contains(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
            .thenReturn(true)
    }

    // Average time between for each frame
    private val deltaTime = 0.017f

    // Test Party object
    private val party = Party(
        angle = Angle.TOP,
        spread = 0,
        speed = 30f,
        maxSpeed = -1f,
        damping = 0.9f,
        size = listOf(Size(sizeInDp = 6, mass = 5f, massVariance = 0f)),
        colors = listOf(Color.RED),
        shapes = listOf(Shape.Square),
        timeToLive = 1000L,
        fadeOutEnabled = false,
        position = Position.Absolute(100f, 100f),
        delay = 0,
        rotation = Rotation(),
        emitter = Emitter(100L).max(10) // Create confetti every 10ms
    )

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
    fun `Create confetti and check its initial state`() {
        val emitter = PartyEmitter(party.emitter, 1f, Random(1L))

        val r1 = emitter.createConfetti(deltaTime, party, drawArea) // 0.017f
        with(r1.first()) {
            Assert.assertEquals(Vector(100f, 100f), location)
            Assert.assertEquals(6f, width)
            Assert.assertEquals(Shape.Square, shape)
            Assert.assertEquals(1000L, lifespan)
            Assert.assertEquals(0.9f, damping)
            Assert.assertEquals(5.6617184f, rotationSpeed2D)
            Assert.assertEquals(0.804353f, rotationSpeed3D)
        }
    }

    @Test
    fun `Initial state confetti with rotation disabled`() {
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 = emitter.createConfetti(
            deltaTime,
            party.copy(rotation = Rotation.disabled()),
            drawArea
        ) // 0.017f

        with(r1.first()) {
            Assert.assertEquals(0.0f, rotationSpeed2D)
            Assert.assertEquals(0.0f, rotationSpeed3D)
        }
    }

    @Test
    fun `Initial state confetti with relative position`() {
        val emitter = PartyEmitter(party.emitter, 1f)

        val r1 = emitter.createConfetti(
            deltaTime,
            party.copy(position = Position.Relative(0.5, 0.5)),
            drawArea
        ) // 0.017f

        with(r1.first()) {
            Assert.assertEquals(Vector(500f, 500f), location)
        }
    }
}
