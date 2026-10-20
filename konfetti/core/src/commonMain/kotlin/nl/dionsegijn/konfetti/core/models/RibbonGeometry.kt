package nl.dionsegijn.konfetti.core.models

import nl.dionsegijn.konfetti.core.Particle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * The short straight pieces a [Shape.Ribbon] streamer is drawn with, filled by [ribbonSegments].
 * Piece i runs from ([startX], [startY]) to ([endX], [endY]) in pixels on the canvas, is [width] pixels
 * thick and has the color in [color]. They're ordered back to front, so draw them in order and the front
 * of each loop covers the back.
 *
 * Reuse one instance for every streamer and frame, it doesn't allocate while drawing.
 */
class RibbonSegments {
    /** How many pieces were filled in */
    var count = 0
        private set

    val startX = FloatArray(MAX_SEGMENTS)
    val startY = FloatArray(MAX_SEGMENTS)
    val endX = FloatArray(MAX_SEGMENTS)
    val endY = FloatArray(MAX_SEGMENTS)
    val width = FloatArray(MAX_SEGMENTS)
    val color = IntArray(MAX_SEGMENTS)

    // The points along the spiral and how far in front of its middle each one is, -1 behind to 1 in front
    private val pointX = FloatArray(MAX_SEGMENTS + 1)
    private val pointY = FloatArray(MAX_SEGMENTS + 1)
    private val depth = FloatArray(MAX_SEGMENTS + 1)
    private val turned = FloatArray(MAX_SEGMENTS + 1)
    private val order = IntArray(MAX_SEGMENTS)

    internal fun clear() {
        count = 0
    }

    internal fun fill(
        particle: Particle,
        ribbon: Shape.Ribbon,
    ) {
        val body = particle.ribbon
        if (body == null || body.length <= 0f) {
            count = 0
            return
        }
        val loops = body.length / body.loopSpacing
        // Tighter stretches need more pieces to stay round
        val segments =
            ceil(loops * SEGMENTS_PER_LOOP * (1 + ribbon.irregularity)).toInt().coerceIn(MIN_SEGMENTS, MAX_SEGMENTS)
        val radius = particle.width / 2
        val strip = ribbon.thickness * body.thicknessScale * body.loopSpacing
        // Shading 0.5 is the usual look, 0 is flat color and 1 twice as strong
        val shading = ribbon.shading / DEFAULT_SHADING * body.contrast
        val frontLight = FRONT_LIGHT * shading
        val backDark = BACK_DARK * shading

        // The body runs from the head along its axis, turned by the drift lean
        val lean = particle.rotation * PI.toFloat() / 180
        val axisX = body.axisX * cos(lean) - body.axisY * sin(lean)
        val axisY = body.axisX * sin(lean) + body.axisY * cos(lean)
        val headX = particle.x + radius
        val headY = particle.y + radius

        // How far round the spiral has turned at each point. Where it's wound tighter it turns faster, and it's
        // scaled so the loops still average out at the ribbon's loop spacing.
        turned[0] = 0f
        for (i in 1..segments) {
            val middle = (i - 0.5f) / segments
            turned[i] = turned[i - 1] + loopDensity(middle, ribbon.irregularity, body.unevenness) / segments
        }
        val toAngle = 2 * PI.toFloat() * loops / turned[segments]

        for (i in 0..segments) {
            val along = i.toFloat() / segments
            val angle = turned[i] * toAngle + body.spin
            // The coil narrows toward the tail and isn't quite round
            val coil =
                radius * (1 - TAIL_TAPER * along) *
                    (1 + RADIUS_WOBBLE * sin(2 * PI.toFloat() * RADIUS_WOBBLE_WAVES * along + body.unevenness * 1.7f))
            val side = coil * cos(angle)
            val offset = offsetAt(body.offsets, along)
            // The spiral's sideways swing is across the axis, the bend pushes it sideways on the screen
            pointX[i] = headX + axisX * along * body.length - axisY * side + offset
            pointY[i] = headY + axisY * along * body.length + axisX * side
            depth[i] = sin(angle)
        }

        for (i in 0 until segments) order[i] = i
        // Back to front, an insertion sort since there are at most MAX_SEGMENTS pieces
        for (i in 1 until segments) {
            val piece = order[i]
            val pieceDepth = depth[piece] + depth[piece + 1]
            var j = i - 1
            while (j >= 0 && depth[order[j]] + depth[order[j] + 1] > pieceDepth) {
                order[j + 1] = order[j]
                j--
            }
            order[j + 1] = piece
        }

        for (k in 0 until segments) {
            val i = order[k]
            val facing = (depth[i] + depth[i + 1]) / 2
            startX[k] = pointX[i]
            startY[k] = pointY[i]
            endX[k] = pointX[i + 1]
            endY[k] = pointY[i + 1]
            // The strip looks widest where it faces you and thin where you see its edge
            width[k] = strip * (EDGE_WIDTH + (1 - EDGE_WIDTH) * abs(facing))
            color[k] = shade(particle.color, if (facing >= 0) facing * frontLight else facing * backDark)
        }
        count = segments
    }

    private fun offsetAt(
        offsets: FloatArray,
        along: Float,
    ): Float {
        if (offsets.size < 2) return 0f
        val position = along * (offsets.size - 1)
        val i = position.toInt().coerceAtMost(offsets.size - 2)
        val fraction = position - i
        return offsets[i] + (offsets[i + 1] - offsets[i]) * fraction
    }

    private companion object {
        const val SEGMENTS_PER_LOOP = 12
        const val MIN_SEGMENTS = 8
        const val MAX_SEGMENTS = 160

        // How thin the strip gets when you see its edge, as a part of its full width
        const val EDGE_WIDTH = 0.25f

        // How much lighter the front gets and how much darker the back, at the default shading
        const val FRONT_LIGHT = 0.3f
        const val BACK_DARK = 0.45f
        const val DEFAULT_SHADING = 0.5f

        // A narrower tail and a not quite round coil
        const val TAIL_TAPER = 0.2f
        const val RADIUS_WOBBLE = 0.08f
        const val RADIUS_WOBBLE_WAVES = 0.9f
    }
}

/**
 * How tightly a streamer's spiral is wound at [along], from its head (0) to its tail (1), compared to an even spiral.
 * 2 means twice as many loops there, so the gaps between them are half as wide. With [irregularity] 0 it's evenly wound.
 * Otherwise two waves make the loops bunch up and stretch out along the body, different for each [unevenness],
 * and the curl gets tighter toward both ends.
 */
internal fun loopDensity(
    along: Float,
    irregularity: Float,
    unevenness: Float,
): Float {
    val waves =
        LONG_WAVE_SIZE * sin(2 * PI.toFloat() * LONG_WAVES * along + unevenness) +
            SHORT_WAVE_SIZE * sin(2 * PI.toFloat() * SHORT_WAVES * along + unevenness * 2.3f)
    val ends = exp(-along / END_LENGTH) + exp(-(1 - along) / END_LENGTH)
    return exp(irregularity * waves) * (1 + irregularity * END_TIGHTEN * ends)
}

// With irregularity 1, the waves make the gaps from about half to almost twice as wide
private const val LONG_WAVE_SIZE = 0.35f
private const val LONG_WAVES = 1.3f
private const val SHORT_WAVE_SIZE = 0.25f
private const val SHORT_WAVES = 2.9f

// The ends curl up to this much tighter, over about this part of the length
private const val END_TIGHTEN = 0.8f
private const val END_LENGTH = 0.08f

/**
 * Fills [out] with the pieces to draw this particle with, when it's a [Shape.Ribbon] streamer.
 * @return false when the particle isn't a streamer, [out] is then left empty
 */
fun Particle.ribbonSegments(out: RibbonSegments): Boolean {
    val ribbon = shape as? Shape.Ribbon
    if (ribbon == null) {
        out.clear()
        return false
    }
    out.fill(this, ribbon)
    return out.count > 0
}

/**
 * [color] made lighter toward white for a positive [amount] or darker toward black for a negative one,
 * between -1 and 1. Alpha stays the same.
 */
fun shade(
    color: Int,
    amount: Float,
): Int {
    val alpha = color ushr 24
    val target = if (amount >= 0) 255 else 0
    val mix = abs(amount).coerceAtMost(1f)

    fun channel(shift: Int): Int {
        val value = (color shr shift) and 0xff
        return (value + (target - value) * mix).toInt().coerceIn(0, 255) shl shift
    }
    return (alpha shl 24) or channel(16) or channel(8) or channel(0)
}
