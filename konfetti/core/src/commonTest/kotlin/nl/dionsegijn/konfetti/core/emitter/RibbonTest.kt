package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Drift
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.RibbonSegments
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.core.models.Vector
import nl.dionsegijn.konfetti.core.models.ribbonSegments
import nl.dionsegijn.konfetti.core.toParticle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RibbonTest {
    private val drawArea = CoreRectImpl(width = 1000f, height = 1000f)

    private fun streamer(
        ribbon: Shape.Ribbon = Shape.Ribbon(),
        location: Vector = Vector(500f, 100f),
        length: Float = 100f,
        sway: Float = 0f,
        spinSpeed: Float = 1f,
        velocity: Vector = Vector(0f, 0f),
    ) = Confetti(
        location = location,
        color = 0xff808080.toInt(),
        width = 16f,
        mass = 5f,
        shape = ribbon,
        lifespan = 100000L,
        damping = 0.9f,
        velocity = velocity,
        rotationSpeed2D = 8f,
        rotationSpeed3D = 1.5f,
        pixelDensity = 1f,
        sway = sway,
        swayFrequency = 1f,
        ribbonLength = length,
        spinSpeed = spinSpeed,
    )

    private fun Confetti.run(
        seconds: Float,
        framesPerSecond: Int = 60,
    ) = apply { repeat((seconds * framesPerSecond).toInt()) { render(1f / framesPerSecond, drawArea) } }

    @Test
    fun `each streamer picks a length between min and max`() {
        val party =
            Party(
                angle = Angle.BOTTOM,
                speed = 0f,
                size = listOf(Size(10)),
                shapes = listOf(Shape.Ribbon(minLength = 40f, maxLength = 80f)),
                emitter = Emitter(100L).max(50),
            )
        val particles = PartySystem(party, 0L, 2f, Random(7)).render(0.2f, CoreRectImpl(0f, 0f, 1000f, 1000f))
        assertEquals(50, particles.size)
        particles.forEach { assertTrue(it.height in 80f..160f, "length ${it.height}") }
        assertTrue(particles.map { it.height }.distinct().size > 10)
    }

    @Test
    fun `a streamer stays upright instead of spinning flat`() {
        val piece = streamer().run(seconds = 1f)
        assertEquals(0f, piece.rotation)
        assertEquals(1f, piece.scaleX)
    }

    // How far a streamer turns in one frame, in radians, unwrapped
    private fun Confetti.spinStep(): Float {
        val before = ribbonBody()!!.spin
        render(1f / 60, drawArea)
        var step = ribbonBody()!!.spin - before
        if (step < -PI) step += (2 * PI).toFloat()
        if (step > PI) step -= (2 * PI).toFloat()
        return step
    }

    @Test
    fun `a falling streamer's loops travel up toward its tail like a screw`() {
        val piece = streamer(spinSpeed = 1f).run(seconds = 1f)
        // Its spin goes down, which moves each loop toward the tail
        assertTrue(piece.spinStep() < 0f)
    }

    @Test
    fun `a streamer spins faster the faster it falls`() {
        val slow = abs(streamer(spinSpeed = 1f).run(seconds = 0.5f).spinStep())
        val fast = abs(streamer(spinSpeed = 1f).run(seconds = 2f).spinStep())
        assertTrue(fast > slow, "slow $slow fast $fast")
    }

    @Test
    fun `a streamer hanging still barely turns`() {
        val piece = streamer(spinSpeed = 1f)
        assertEquals(0f, piece.spinStep(), 0.01f)
    }

    @Test
    fun `its pieces go from the darker back to the lighter front`() {
        val segments = RibbonSegments()
        assertTrue(streamer().run(seconds = 0.2f).toParticle().ribbonSegments(segments))
        val first = segments.color[0] and 0xff
        val last = segments.color[segments.count - 1] and 0xff
        assertTrue(first < 0x80, "back $first")
        assertTrue(last > 0x80, "front $last")
        val widths = segments.width.take(segments.count)
        assertTrue(widths.max() > widths.min() * 2)
    }

    @Test
    fun `other shapes have no streamer pieces`() {
        val square =
            Confetti(location = Vector(0f, 0f), color = 0, width = 10f, mass = 5f, shape = Shape.Square, damping = 0.9f, pixelDensity = 1f)
        assertFalse(square.toParticle().ribbonSegments(RibbonSegments()))
    }

    @Test
    fun `its body trails behind its head when it sways`() {
        val bending = streamer(ribbon = Shape.Ribbon(bend = 1f), sway = 30f).run(seconds = 1.3f)
        val offsets = bending.ribbonBody()!!.offsets
        assertEquals(0f, offsets.first())
        assertTrue(abs(offsets.last()) > 5f, "tail ${offsets.last()}")

        val stiff = streamer(ribbon = Shape.Ribbon(bend = 0f), sway = 30f).run(seconds = 1.3f)
        assertTrue(stiff.ribbonBody()!!.offsets.all { it == 0f })
    }

    @Test
    fun `the body bends the same at 120 fps as at 60 fps`() {
        val at60 = streamer(ribbon = Shape.Ribbon(bend = 1f), sway = 30f).run(seconds = 1.3f).ribbonBody()!!
        val at120 = streamer(ribbon = Shape.Ribbon(bend = 1f), sway = 30f).run(seconds = 1.3f, framesPerSecond = 120).ribbonBody()!!
        at60.offsets.zip(at120.offsets).forEach { (a, b) -> assertEquals(a, b, 2f) }
    }

    @Test
    fun `a streamer lives until its tail has left the bottom`() {
        // The head is just below the view, the body hangs up into it
        val piece = streamer(location = Vector(500f, 1030f), length = 100f)
        piece.render(1f / 60, drawArea)
        assertFalse(piece.isDead())
        assertTrue(piece.drawParticle)
    }

    private fun drifting(ribbon: Shape.Ribbon) =
        PartyEmitter(Emitter(100L).max(1), 1f, Random(5))
            .createConfetti(
                0.2f,
                Party(speed = 0f, shapes = listOf(ribbon), drift = Drift(enabled = true, variance = 0f), emitter = Emitter(100L).max(1)),
                drawArea,
            ).single()

    @Test
    fun `a streamer doesn't tip into its swing`() {
        val piece = drifting(Shape.Ribbon()).run(seconds = 2f)
        assertEquals(0f, piece.tiltAngle)
    }

    @Test
    fun `a longer streamer swings slower`() {
        // Counts how often the sway turns around in 6 seconds
        fun turns(ribbon: Shape.Ribbon): Int {
            val piece = drifting(ribbon)
            var last = piece.location.x
            var direction = 0f
            var turns = 0
            repeat(360) {
                piece.render(1f / 60, drawArea)
                val step = piece.location.x - last
                if (step * direction < 0f) turns++
                if (step != 0f) direction = step
                last = piece.location.x
            }
            return turns
        }
        assertTrue(turns(Shape.Ribbon(minLength = 150f, maxLength = 150f)) < turns(Shape.Ribbon(minLength = 30f, maxLength = 30f)))
    }

    @Test
    fun `a streamer doesn't jump sideways when it starts to fall`() {
        val piece = drifting(Shape.Ribbon())
        var last = piece.location.x
        repeat(180) {
            piece.render(1f / 60, drawArea)
            assertTrue(abs(piece.location.x - last) < 2f, "moved ${piece.location.x - last} in one frame")
            last = piece.location.x
        }
    }

    // Tall enough that it doesn't fall out of view while it's watched
    private val tallArea = CoreRectImpl(width = 1000f, height = 5000f)

    private fun shotUp() =
        PartyEmitter(Emitter(100L).max(1), 1f, Random(4)).createConfetti(
            0.2f,
            Party(
                angle = 270,
                spread = 0,
                speed = 40f,
                damping = 0.92f,
                shapes = listOf(Shape.Ribbon()),
                position = Position.Relative(0.5, 0.9),
                emitter = Emitter(100L).max(1),
            ),
            tallArea,
        ).single()

    @Test
    fun `a streamer shot up trails its tail below its head right away`() {
        val piece = shotUp()
        piece.render(1f / 60, tallArea)
        assertTrue(piece.ribbonBody()!!.axisY > 0.9f, "axis ${piece.ribbonBody()!!.axisY}")
    }

    @Test
    fun `a streamer spins faster shooting up than falling`() {
        val shooting = abs(streamer(spinSpeed = 1f, location = Vector(500f, 900f), velocity = Vector(0f, -40f)).spinStep())
        val falling = abs(streamer(spinSpeed = 1f).run(seconds = 2f).spinStep())
        assertTrue(shooting > falling * 2, "shooting $shooting falling $falling")
    }

    @Test
    fun `a streamer flops over at the top of its arc instead of flipping`() {
        val piece = shotUp()
        var last = piece.ribbonBody()!!
        repeat(180) { frame ->
            piece.render(1f / 60, tallArea)
            val body = piece.ribbonBody()!!
            // The body never turns more than a few degrees in one frame
            val dot = last.axisX * body.axisX + last.axisY * body.axisY
            assertTrue(dot > 0.99f, "frame $frame turned ${acos(dot.coerceIn(-1f, 1f)) * 57.3f} degrees")
            last = body
        }
        assertTrue(last.axisY < -0.9f, "hangs up again ${last.axisY}")
    }

    private fun party(ribbon: Shape.Ribbon) =
        PartySystem(
            Party(speed = 0f, size = listOf(Size(12)), shapes = listOf(ribbon), emitter = Emitter(100L).max(30)),
            0L,
            1f,
            Random(9),
        ).render(0.2f, CoreRectImpl(0f, 0f, 1000f, 1000f))

    @Test
    fun `without variance every streamer is alike apart from its length`() {
        val streamers = party(Shape.Ribbon(variance = 0f))
        assertEquals(1, streamers.map { it.width }.distinct().size)
        assertEquals(1, streamers.map { it.ribbon!!.loopSpacing }.distinct().size)
        assertEquals(1, streamers.map { it.ribbon!!.thicknessScale }.distinct().size)
        assertEquals(1, streamers.map { it.ribbon!!.contrast }.distinct().size)
        assertTrue(streamers.map { it.height }.distinct().size > 10)
    }

    @Test
    fun `with variance streamers differ from each other`() {
        val streamers = party(Shape.Ribbon(variance = 1f))
        val widths = streamers.map { it.width }
        val spacings = streamers.map { it.ribbon!!.loopSpacing }
        assertTrue(widths.max() > widths.min() * 1.5f, "widths $widths")
        assertTrue(spacings.max() > spacings.min() * 2f, "spacings $spacings")
        assertTrue(streamers.map { it.ribbon!!.thicknessScale }.distinct().size > 10)
        assertTrue(streamers.map { it.ribbon!!.contrast }.distinct().size > 10)
    }

    // The difference between the lightest and darkest piece of one streamer, in its red channel
    private fun contrastOf(shading: Float): Int {
        val segments = RibbonSegments()
        streamer(ribbon = Shape.Ribbon(shading = shading)).run(seconds = 0.2f).toParticle().ribbonSegments(segments)
        val reds = (0 until segments.count).map { (segments.color[it] shr 16) and 0xff }
        return reds.max() - reds.min()
    }

    @Test
    fun `shading sets how 3D a streamer looks`() {
        assertEquals(0, contrastOf(shading = 0f))
        assertTrue(contrastOf(shading = 1f) > contrastOf(shading = 0.5f))
    }
}
