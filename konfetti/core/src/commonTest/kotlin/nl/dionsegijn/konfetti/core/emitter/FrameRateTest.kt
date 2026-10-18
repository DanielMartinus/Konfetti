package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.test.Test
import kotlin.test.assertEquals

class FrameRateTest {
    private val drawArea = CoreRectImpl(width = 1000f, height = 1000f)
    private val velocities = listOf(Vector(0f, -20f), Vector(12f, -8f), Vector(-15f, 3f), Vector(25f, -25f))

    // Runs one second of confetti at the given frame rate
    private fun oneSecondAt(
        framesPerSecond: Int,
        damping: Float,
    ): List<Confetti> {
        val pieces =
            velocities.map {
                Confetti(
                    location = Vector(500f, 300f),
                    color = 0xfce18a,
                    width = 16f,
                    mass = 5f,
                    shape = Shape.Square,
                    lifespan = 500L,
                    velocity = Vector(it.x, it.y),
                    damping = damping,
                    rotationSpeed2D = 1.7f,
                    rotationSpeed3D = 0.37f,
                    pixelDensity = 1f,
                )
            }
        repeat(framesPerSecond) { pieces.forEach { it.render(1f / framesPerSecond, drawArea) } }
        return pieces
    }

    // Gravity still adds up per step, so a smaller step lands a little off
    private fun assertSameMotion(
        expected: List<Confetti>,
        actual: List<Confetti>,
        tolerance: Float,
    ) {
        expected.zip(actual).forEach { (a, b) ->
            assertEquals(a.location.x, b.location.x, tolerance)
            assertEquals(a.location.y, b.location.y, tolerance)
            assertEquals(a.rotation, b.rotation, 0.01f)
            assertEquals(a.alpha, b.alpha)
        }
    }

    @Test
    fun `confetti at 120 fps moves like confetti at 60 fps`() {
        assertSameMotion(oneSecondAt(60, damping = 0.9f), oneSecondAt(120, damping = 0.9f), tolerance = 2f)
    }

    @Test
    fun `confetti without damping at 120 fps moves like confetti at 60 fps`() {
        // Without damping it travels about 1000px, so the gravity difference grows to a few pixels
        assertSameMotion(oneSecondAt(60, damping = 1f), oneSecondAt(120, damping = 1f), tolerance = 5f)
    }
}
