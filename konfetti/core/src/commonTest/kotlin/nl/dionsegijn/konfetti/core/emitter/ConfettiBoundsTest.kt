package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfettiBoundsTest {
    private val drawArea = CoreRectImpl(0f, 0f, 100f, 100f)

    // A 20px piece at rest, so one frame barely moves it
    private fun confettiAt(
        x: Float,
        y: Float,
    ) = Confetti(
        location = Vector(x, y),
        color = 0xff0000,
        width = 20f,
        mass = 5f,
        shape = Shape.Square,
        lifespan = 10_000L,
        damping = 1f,
        pixelDensity = 1f,
    ).apply { render(0.016f, drawArea) }

    @Test
    fun `a piece half above the top edge is drawn`() {
        assertTrue(confettiAt(40f, -10f).drawParticle)
    }

    @Test
    fun `a piece half past the left edge is drawn`() {
        assertTrue(confettiAt(-10f, 40f).drawParticle)
    }

    @Test
    fun `a piece half past the right edge is drawn`() {
        assertTrue(confettiAt(90f, 40f).drawParticle)
    }

    @Test
    fun `a piece half below the bottom edge is drawn and alive`() {
        val confetti = confettiAt(40f, 90f)
        assertTrue(confetti.drawParticle)
        assertFalse(confetti.isDead())
    }

    @Test
    fun `a piece whose corner still reaches over the bottom edge stays alive`() {
        // The top-left point is below the view, but a rotated corner can still show
        val confetti = confettiAt(40f, 101f)
        assertTrue(confetti.drawParticle)
        assertFalse(confetti.isDead())
    }

    @Test
    fun `a piece fully below the view is dead`() {
        assertTrue(confettiAt(40f, 150f).isDead())
    }

    @Test
    fun `a piece fully above the view is hidden but stays alive to fall back in`() {
        val confetti = confettiAt(40f, -50f)
        assertFalse(confetti.drawParticle)
        assertFalse(confetti.isDead())
    }

    @Test
    fun `a piece fully outside the left or right edge is hidden`() {
        assertFalse(confettiAt(-50f, 40f).drawParticle)
        assertFalse(confettiAt(150f, 40f).drawParticle)
    }
}
