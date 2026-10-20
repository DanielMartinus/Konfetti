package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.Drift
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DriftTest {
    private val drawArea = CoreRectImpl(width = 1000f, height = 100000f)

    private fun piece(
        sway: Float = 0f,
        swayFrequency: Float = 0f,
        tilt: Float = 0f,
        drag: Float = 0f,
        velocity: Vector = Vector(0f, 0f),
        shape: Shape = Shape.Square,
    ) = Confetti(
        location = Vector(500f, 0f),
        color = 0xfce18a,
        width = 16f,
        mass = 5f,
        shape = shape,
        lifespan = 100000L,
        velocity = velocity,
        damping = 0.9f,
        rotationSpeed2D = 0f,
        rotationSpeed3D = 0f,
        pixelDensity = 1f,
        sway = sway,
        swayFrequency = swayFrequency,
        tilt = tilt,
        drag = drag,
    )

    // Renders a piece for [seconds] at [framesPerSecond], keeping the x of every frame
    private fun Confetti.fall(
        seconds: Float,
        framesPerSecond: Int = 60,
    ): List<Float> =
        (0 until (seconds * framesPerSecond).toInt()).map {
            render(1f / framesPerSecond, drawArea)
            location.x
        }

    @Test
    fun `a drifting piece swings to both sides of where it falls`() {
        // The swing is a mix of two, so it doesn't always reach its full size
        val xs = piece(sway = 20f, swayFrequency = 1f).fall(seconds = 4f)
        assertTrue(xs.max() > 510f, "max ${xs.max()}")
        assertTrue(xs.min() < 490f, "min ${xs.min()}")
    }

    @Test
    fun `a piece without drift falls straight down`() {
        val xs = piece().fall(seconds = 2f)
        assertTrue(xs.all { it == 500f })
    }

    @Test
    fun `drifting looks the same at 120 fps as at 60 fps`() {
        val at60 = piece(sway = 20f, swayFrequency = 1.3f, tilt = 30f).apply { fall(seconds = 1f) }
        val at120 = piece(sway = 20f, swayFrequency = 1.3f, tilt = 30f).apply { fall(seconds = 1f, framesPerSecond = 120) }
        assertEquals(at60.location.x, at120.location.x, 1f)
        assertEquals(at60.location.y, at120.location.y, 2f)
        assertEquals(at60.tiltAngle, at120.tiltAngle, 1f)
    }

    @Test
    fun `a piece shooting up isn't pulled sideways`() {
        val shooting = piece(sway = 20f, swayFrequency = 1f, velocity = Vector(0f, -30f))
        shooting.render(1f / 60, drawArea)
        assertEquals(500f, shooting.location.x)
    }

    @Test
    fun `drag makes confetti fall slower`() {
        val free = piece().apply { fall(seconds = 2f) }
        val dragged = piece(drag = 0.6f).apply { fall(seconds = 2f) }
        assertTrue(dragged.location.y < free.location.y * 0.6f)
    }

    @Test
    fun `a drifting piece leans into its swing`() {
        val tilts =
            piece(sway = 20f, swayFrequency = 1f, tilt = 30f).run {
                // The swing grows in over the first second, so look at the next three
                fall(seconds = 1f)
                (0 until 180).map {
                    render(1f / 60, drawArea)
                    tiltAngle
                }
            }
        assertTrue(tilts.max() > 12f, "max ${tilts.max()}")
        assertTrue(tilts.min() < -12f, "min ${tilts.min()}")
    }

    @Test
    fun `drift settings stay in range`() {
        assertFailsWith<IllegalArgumentException> { Drift(variance = 2f) }
        assertFailsWith<IllegalArgumentException> { Drift(drag = -0.1f) }
        assertFailsWith<IllegalArgumentException> { Drift(sway = -1f) }
    }

    private fun driftingParty(count: Int) =
        PartyEmitter(Emitter(100L).max(count), 1f, Random(11)).createConfetti(
            0.2f,
            Party(angle = 90, spread = 0, speed = 0f, drift = Drift(enabled = true), emitter = Emitter(100L).max(count)),
            drawArea,
        )

    // How many dp a piece falls in the next half second
    private fun Confetti.fallOver(seconds: Float): Float {
        val start = location.y
        fall(seconds = seconds)
        return location.y - start
    }

    @Test
    fun `drifting pieces settle at their own fall speed`() {
        val pieces = driftingParty(20)
        pieces.forEach { it.fall(seconds = 4f) }
        val speeds = pieces.map { it.fallOver(0.5f) }
        assertTrue(speeds.max() > speeds.min() * 1.3f, "speeds $speeds")
        // And they stop speeding up once settled
        pieces.zip(speeds).forEach { (piece, speed) -> assertEquals(speed, piece.fallOver(0.5f), speed * 0.05f) }
    }

    @Test
    fun `the sway isn't one even swing`() {
        val xs = piece(sway = 20f, swayFrequency = 1f).fall(seconds = 6f).drop(60)
        // The turning points of the swing, one each time it changes direction
        val turns =
            xs.zipWithNext().zipWithNext().filter {
                    (a, b) ->
                (a.second - a.first) * (b.second - b.first) < 0
            }.map { it.first.second }
        val sizes = turns.zipWithNext { a, b -> abs(b - a) }
        assertTrue(sizes.max() - sizes.min() > 4f, "swing sizes $sizes")
    }
}
