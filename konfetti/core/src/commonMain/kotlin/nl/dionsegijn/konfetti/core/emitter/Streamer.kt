package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.RibbonBody
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The body of a [Shape.Ribbon] streamer. It hangs from its head, spins around its own length
 * and trails behind the head's drift swing. [Confetti] moves the head like any other piece.
 * Each streamer draws its own length, spin and curl from [random] when it's made.
 * Only Konfetti makes and reads them.
 */
class Streamer internal constructor(
    ribbon: Shape.Ribbon,
    velocity: Vector,
    random: Random,
    private val pixelDensity: Float,
) {
    // A factor around 1 for this streamer, up to [most] either way when the ribbon's variance is 1
    private fun Shape.Ribbon.spread(
        random: Random,
        most: Float,
    ): Float = 1f + variance * most * (random.nextFloat() * 2f - 1f)

    /** How long it is in pixels */
    internal val length = (ribbon.minLength + (ribbon.maxLength - ribbon.minLength) * random.nextFloat()) * pixelDensity

    // How far it has turned around its own length, and how many turns a second it spins at a usual speed
    private var spin = random.nextFloat() * TWO_PI
    private val spinSpeed = ribbon.spin * ribbon.spread(random, SPIN_SPREAD)

    // No two streamers are curled quite the same
    private val loopSpacing = LOOP_SPACING * ribbon.spread(random, LOOP_SPREAD) * pixelDensity
    private val unevenness = random.nextFloat() * TWO_PI
    private val thicknessScale = ribbon.spread(random, THICKNESS_SPREAD)
    private val bend = (BEND * ribbon.spread(random, BEND_SPREAD)).coerceIn(0f, 1f)
    private val contrast = ribbon.spread(random, CONTRAST_SPREAD)

    /** A long streamer swings slower, like a longer pendulum */
    internal val swaySlowdown = sqrt(PENDULUM_LENGTH * pixelDensity / length).coerceAtMost(1f)

    // Past sway offsets of the head, newest at trailNewest, one every TRAIL_STEP seconds
    private val trail = if (bend > 0f) FloatArray(TRAIL_SIZE) else null
    private var trailNewest = 0
    private var trailClock = 0f

    // The way its body runs from its head. It starts trailing behind the way it's launched,
    // so a streamer shot up doesn't fly tail first and then flip over
    private var hangX = 0f
    private var hangY = -1f

    // Which way it tips over at the top of a straight up shot, from its own random start
    private val flopSide = if (spin > PI) 1f else -1f

    init {
        val targetX = -velocity.x
        val targetY = -velocity.y - HANG
        val targetLength = sqrt(targetX * targetX + targetY * targetY)
        if (targetLength > 0f) {
            hangX = targetX / targetLength
            hangY = targetY / targetLength
        }
    }

    internal fun update(
        velocity: Vector,
        deltaTime: Float,
        steps: Float,
        swayOffset: Float,
    ) {
        hang(velocity, deltaTime, steps)
        spin = (spin + TWO_PI * spinRate(velocity) * deltaTime) % TWO_PI
        recordTrail(deltaTime, swayOffset)
    }

    /**
     * How it hangs and spins right now. Its body runs from the head along the hang direction,
     * and each point along it sits where the head's swing was a little earlier, so the tail follows.
     */
    internal fun body(swayOffset: Float): RibbonBody {
        val pathX = FloatArray(BODY_POINTS)
        val pathY = FloatArray(BODY_POINTS)
        for (i in 1 until BODY_POINTS) {
            val along = i / (BODY_POINTS - 1f)
            val lag = if (trail != null) swayOffsetAgo(trail, along * bend * MAX_LAG) - swayOffset else 0f
            pathX[i] = hangX * along * length + lag
            pathY[i] = hangY * along * length
        }
        return RibbonBody(
            spin = spin,
            length = length,
            loopSpacing = loopSpacing,
            pathX = pathX,
            pathY = pathY,
            unevenness = unevenness,
            thicknessScale = thicknessScale,
            contrast = contrast,
        )
    }

    // Spins like a screw driven through the air, faster the faster it moves along its body
    private fun spinRate(velocity: Vector): Float {
        val alongBody = -(velocity.x * hangX + velocity.y * hangY)
        val boost = sqrt(abs(alongBody) / SPIN_SPEED_REFERENCE).coerceAtMost(MAX_SPIN_BOOST)
        return if (alongBody < 0f) spinSpeed * boost else -spinSpeed * boost
    }

    // Swings the body round to trail behind the way it moves, with gravity pulling it to hang up from the head
    private fun hang(
        velocity: Vector,
        deltaTime: Float,
        steps: Float,
    ) {
        val targetX = -velocity.x
        val targetY = -velocity.y - HANG
        if (targetX == 0f && targetY == 0f) return
        // Like a rope, it turns by how far it travels, so it flops over at the top of its arc instead of flipping
        val travelled = sqrt(velocity.x * velocity.x + velocity.y * velocity.y) * steps * pixelDensity
        val ease = 1f - exp(-(travelled / (FOLLOW_DISTANCE * length) + deltaTime / HANG_EASE))
        // Turn the short way round. Straight up or down there's no short way, so it tips to its own side.
        val current = atan2(hangY, hangX)
        var turn = atan2(targetY, targetX) - current
        while (turn > PI) turn -= TWO_PI
        while (turn < -PI) turn += TWO_PI
        if (abs(turn) > PI - TIP_OVER) turn = flopSide * abs(turn)
        val angle = current + (turn * ease).coerceIn(-MAX_TURN * deltaTime, MAX_TURN * deltaTime)
        hangX = cos(angle)
        hangY = sin(angle)
    }

    // Keeps the sway offset every TRAIL_STEP, so it's the same at any frame rate
    private fun recordTrail(
        deltaTime: Float,
        swayOffset: Float,
    ) {
        val trail = trail ?: return
        trailClock += deltaTime
        while (trailClock >= TRAIL_STEP) {
            trailClock -= TRAIL_STEP
            trailNewest = (trailNewest + 1) % TRAIL_SIZE
            trail[trailNewest] = swayOffset
        }
    }

    // The sway offset [seconds] ago, between the two samples around it
    private fun swayOffsetAgo(
        trail: FloatArray,
        seconds: Float,
    ): Float {
        val back = seconds / TRAIL_STEP
        val i = floor(back).toInt().coerceAtMost(TRAIL_SIZE - 2)
        val fraction = back - i
        val newer = trail[(trailNewest - i + TRAIL_SIZE) % TRAIL_SIZE]
        val older = trail[(trailNewest - i - 1 + TRAIL_SIZE) % TRAIL_SIZE]
        return newer + (older - newer) * fraction
    }

    private companion object {
        private const val TWO_PI = (2 * PI).toFloat()

        // The distance between two loops of the curl in dp, and how far the tail trails behind the swing
        private const val LOOP_SPACING = 10f
        private const val BEND = 0.5f

        // How much a streamer can differ from the others, either way, when the ribbon's variance is 1
        private const val SPIN_SPREAD = 0.8f
        private const val LOOP_SPREAD = 0.6f
        private const val THICKNESS_SPREAD = 0.5f
        private const val BEND_SPREAD = 1f
        private const val CONTRAST_SPREAD = 0.5f

        // A streamer this long in dp swings as fast as Drift says, longer ones swing slower
        private const val PENDULUM_LENGTH = 12f

        // The tail follows the head's swing up to this many seconds late, sampled 60 times a second
        private const val MAX_LAG = 0.5f
        private const val TRAIL_STEP = 1f / 60
        private const val TRAIL_SIZE = 32
        private const val BODY_POINTS = 13

        // How much its own fall keeps the body pointing up, against the way it's moving
        private const val HANG = 2f

        // Seconds the body takes to settle when it hardly moves
        private const val HANG_EASE = 0.4f

        // How far it travels, as a part of its length, to swing its body round behind its head
        private const val FOLLOW_DISTANCE = 0.5f

        // How close to straight behind it, in radians, it picks its own side to tip over to
        private const val TIP_OVER = 0.05f

        // The fastest the body swings round, in radians a second
        private const val MAX_TURN = 4f

        // Moving along its body this fast, in dp per 60 fps step, it spins at its spin speed.
        // Faster it spins more, by the square root, up to MAX_SPIN_BOOST times as fast.
        private const val SPIN_SPEED_REFERENCE = 2f
        private const val MAX_SPIN_BOOST = 6f
    }
}
