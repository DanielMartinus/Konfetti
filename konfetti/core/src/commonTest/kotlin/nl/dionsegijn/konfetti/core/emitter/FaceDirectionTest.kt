package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FaceDirectionTest {
    private val drawArea = CoreRectImpl(width = 10_000f, height = 10_000f)

    private fun confetti(
        velocity: Vector,
        faceDirection: Boolean,
    ) = Confetti(
        location = Vector(5_000f, 5_000f),
        color = 0xff0000,
        width = 20f,
        mass = 5f,
        shape = Shape.Rectangle(0.2f),
        lifespan = 10_000L,
        velocity = velocity,
        damping = 0.9f,
        rotationSpeed2D = 8f,
        faceDirection = faceDirection,
        pixelDensity = 1f,
    )

    @Test
    fun `a piece points the way it's shot from the start`() {
        assertEquals(0f, confetti(Vector(10f, 0f), faceDirection = true).rotation, 0.01f)
        assertEquals(-90f, confetti(Vector(0f, -10f), faceDirection = true).rotation, 0.01f)
        assertEquals(135f, confetti(Vector(-10f, 10f), faceDirection = true).rotation, 0.01f)
    }

    @Test
    fun `a piece shot sideways bends round to point down as it falls`() {
        val piece = confetti(Vector(10f, 0f), faceDirection = true)
        val angles =
            List(180) {
                piece.render(1 / 60f, drawArea)
                piece.rotation
            }
        assertTrue(angles.zipWithNext().all { (a, b) -> b >= a }, "the angle only turns down")
        assertTrue(angles.first() < 5f, "starts pointing right, was ${angles.first()}")
        assertTrue(angles.last() > 85f, "ends pointing down, was ${angles.last()}")
    }

    @Test
    fun `without faceDirection a piece spins at its own speed`() {
        val piece = confetti(Vector(10f, 0f), faceDirection = false)
        assertEquals(0f, piece.rotation)
        piece.render(1 / 60f, drawArea)
        assertEquals(8f, piece.rotation, 0.01f)
    }

    @Test
    fun `the emitter passes faceDirection on`() {
        val party =
            Party(
                angle = Angle.BOTTOM,
                spread = 0,
                speed = 10f,
                position = Position.Absolute(0f, 0f),
                rotation = Rotation(enabled = false, faceDirection = true),
                emitter = Emitter(100L).max(1),
            )
        val piece = PartyEmitter(party.emitter, 1f, Random(1L)).createConfetti(0.2f, party, drawArea).single()
        assertEquals(90f, piece.rotation, 0.01f)
    }
}
