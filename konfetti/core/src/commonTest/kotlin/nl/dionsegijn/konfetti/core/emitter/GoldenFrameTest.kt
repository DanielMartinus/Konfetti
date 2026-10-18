package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * State recorded from the frame based physics at 60 fps.
 * Motion changes must keep producing these so confetti looks the same at 60 Hz.
 */
class GoldenFrameTest {
    private data class Golden(
        val x: Float,
        val y: Float,
        val rotation: Float,
        val scaleX: Float,
        val alpha: Int,
    )

    private val frame60 =
        listOf(
            Golden(500.0f, 169.6742f, 101.999954f, 0.26000023f, 255),
            Golden(607.80585f, 289.81784f, 173.99995f, 0.17500019f, 255),
            Golden(365.2426f, 368.07712f, 258.00012f, 0.068749785f, 255),
            Golden(724.59576f, 120.26873f, 353.9998f, 0.23999977f, 255),
            Golden(446.09708f, 247.02824f, 73.0f, 0.043750167f, 255),
        )

    private val frame120 =
        listOf(
            Golden(500.0f, 345.39642f, 203.99977f, 0.47999954f, 120),
            Golden(607.99963f, 509.74506f, 347.9996f, 0.6499997f, 120),
            Golden(365.00043f, 514.82983f, 154.80005f, 0.88125f, 120),
            Golden(724.99927f, 279.90607f, 342.1998f, 0.5350001f, 120),
            Golden(446.00018f, 442.44037f, 146.00003f, 0.9124998f, 120),
        )

    // Speeds don't divide into 360 or the width, so no piece sits right on a wrap
    private val velocities = listOf(Vector(0f, -20f), Vector(12f, -8f), Vector(-15f, 3f), Vector(25f, -25f), Vector(-6f, -12f))
    private val masses = listOf(5f, 4f, 6f, 5.5f, 4.5f)
    private val rotationSpeeds2D = listOf(1.7f, 2.9f, 4.3f, 5.9f, 7.3f)
    private val rotationSpeeds3D = listOf(0.37f, 0.66f, 0.95f, 1.24f, 1.53f)

    @Test
    fun `confetti at 60 fps lands on the recorded state`() {
        val drawArea = CoreRectImpl(width = 1000f, height = 1000f)
        val pieces =
            velocities.indices.map { i ->
                Confetti(
                    location = Vector(500f, 300f),
                    color = 0xfce18a,
                    width = 16f,
                    mass = masses[i],
                    shape = Shape.Square,
                    lifespan = 1500L,
                    velocity = velocities[i],
                    damping = 0.9f,
                    rotationSpeed2D = rotationSpeeds2D[i],
                    rotationSpeed3D = rotationSpeeds3D[i],
                    pixelDensity = 1f,
                )
            }

        for (frame in 1..120) {
            pieces.forEach { it.render(1f / 60f, drawArea) }
            if (frame == 60) assertGolden(frame60, pieces)
            if (frame == 120) assertGolden(frame120, pieces)
        }
    }

    private fun assertGolden(
        expected: List<Golden>,
        pieces: List<Confetti>,
    ) {
        expected.zip(pieces).forEach { (golden, confetti) ->
            assertEquals(golden.x, confetti.location.x, TOLERANCE)
            assertEquals(golden.y, confetti.location.y, TOLERANCE)
            assertEquals(golden.rotation, confetti.rotation, TOLERANCE)
            assertEquals(golden.scaleX, confetti.scaleX, TOLERANCE)
            assertEquals(golden.alpha, confetti.alpha)
        }
    }

    companion object {
        // Leaves room for float differences between platforms
        private const val TOLERANCE = 0.01f
    }
}
