package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.RibbonBody
import nl.dionsegijn.konfetti.core.models.CoreRect
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Confetti holds all data to the current state of the particle
 * Each frame update triggers the `render` method, which recalculates the particle's properties based on its current state.
 *
 * @property location The current position of the particle as a Vector object that contains x and y coordinates.
 * @property color The color of the particle, represented as an integer. (AARRGGBB)
 * @property width The width of the particle in pixels.
 * @property mass The mass of the particle, affecting how forces like gravity influence it. A particle with more mass will move slower under the same force.
 * @property shape The geometric shape of the particle.
 * @property lifespan The duration the particle should exist for in milliseconds.
 * @property fadeOut If true, the particle will gradually become transparent over its lifespan.
 * @property acceleration The current acceleration of the particle.
 * @property velocity The current velocity of the particle.
 * @property damping A factor that reduces the particle's velocity over time, simulating air resistance. A higher damping value will slow down the particle faster.
 * @property rotationSpeed3D The speed at which the particle rotates.
 * @property rotationSpeed2D The speed at which the particle rotates in 2D space.
 * @property faceDirection If true, the 2D rotation follows the way the particle is moving in place of [rotationSpeed2D].
 * @property pixelDensity The pixel density of the device's screen. This is used to ensure that the particle's movement looks consistent across devices with different screen densities.
 * @property sway How far the particle swings to each side while it falls, in dp. 0 means it falls straight.
 * @property swayFrequency How many times a second the particle swings back and forth.
 * @property swayPhase Where in its swing the particle starts, in radians.
 * @property tilt How many degrees the particle leans into its swing.
 * @property drag Between 0 and 1, takes that part of gravity away so the particle floats down slower.
 * @property fallSpeed The speed in dp a second the particle settles at once it falls, 0 to keep speeding up like other confetti.
 * @property harmonicPhase Where the smaller second swing of the drift starts, in radians, so the sway isn't one even sine.
 * @property wanderPhase Where the slow sideways wander of the drift starts, in radians.
 * @property ribbonLength How long a [Shape.Ribbon] streamer is, in pixels.
 * @property spinPhase How far a [Shape.Ribbon] has turned around its own length at the start, in radians.
 * @property spinSpeed How many turns a second a [Shape.Ribbon] spins when it falls at a usual speed. It spins faster
 * when it moves faster along its length, and like a screw its loops travel back toward its tail as it moves.
 * @property loopScale How much wider or tighter this streamer's loops are than its [Shape.Ribbon.loopSpacing].
 * @property shapePhases Two phases in radians that make this streamer's spiral a little uneven along its length.
 * @property ribbonBend How far this streamer's body trails behind its head, between 0 and 1. Null takes the [Shape.Ribbon.bend].
 * @property thicknessScale How much thicker or thinner this streamer's strip is than its [Shape.Ribbon.thickness].
 * @property contrast How much stronger or weaker this streamer's shading is than its [Shape.Ribbon.shading].
 */
class Confetti(
    var location: Vector,
    private val color: Int,
    val width: Float,
    private val mass: Float,
    val shape: Shape,
    var lifespan: Long = -1L,
    val fadeOut: Boolean = true,
    private var acceleration: Vector = Vector(0f, 0f),
    var velocity: Vector = Vector(),
    var damping: Float,
    val rotationSpeed3D: Float = 1f,
    val rotationSpeed2D: Float = 1f,
    val pixelDensity: Float,
    private val faceDirection: Boolean = false,
    private val sway: Float = 0f,
    private val swayFrequency: Float = 0f,
    swayPhase: Float = 0f,
    private val tilt: Float = 0f,
    private val drag: Float = 0f,
    private val ribbonLength: Float = 0f,
    spinPhase: Float = 0f,
    private val spinSpeed: Float = 0f,
    private val fallSpeed: Float = 0f,
    harmonicPhase: Float = 0f,
    wanderPhase: Float = 0f,
    private val loopScale: Float = 1f,
    private val shapePhases: Float = 0f,
    ribbonBend: Float? = null,
    private val thicknessScale: Float = 1f,
    private val contrast: Float = 1f,
) {
    companion object {
        // Motion was tuned per frame at 60 fps, so one step is 1/60 of a second
        internal const val STEPS_PER_SECOND = 60f
        private const val GRAVITY = 0.02f
        private const val ALPHA_DECREMENT = 5
        private const val MILLIS_IN_SECOND = 1000
        private const val FULL_CIRCLE = 360f
        private const val DEGREES_PER_RADIAN = 180 / PI
        private const val TWO_PI = (2 * PI).toFloat()

        // Half the diagonal of a square, so the reach covers the piece at any rotation
        private const val HALF_DIAGONAL = 0.7072f

        // A streamer remembers where its swing was, so its body can follow its head this many seconds late
        private const val MAX_LAG = 0.5f
        private const val TRAIL_STEP = 1f / 60
        private const val TRAIL_SIZE = 32
        private const val BODY_POINTS = 9

        // How much a streamer's own fall keeps its body pointing up, against the way it's moving
        private const val HANG = 2f

        // How many seconds a streamer's body takes to settle when it hardly moves, gravity pulling it straight
        private const val HANG_EASE = 0.4f

        // How far a streamer travels, as a part of its length, to swing its body round behind its head
        private const val FOLLOW_DISTANCE = 0.5f

        // How close to straight behind it, in radians, a streamer picks its own side to tip over to
        private const val TIP_OVER = 0.05f

        // The fastest a streamer's body swings round, in radians a second
        private const val MAX_TURN = 4f

        // How many seconds the drift swing takes to grow once a piece falls, so it doesn't jump sideways
        private const val SWAY_EASE = 0.5f

        // The drift swing is a main sine plus a smaller faster one, so no two swings are the same size
        private const val HARMONIC_SPEED = 2.3f
        private const val HARMONIC_SIZE = 0.35f

        // And a slow wander to one side, so a piece drifts off instead of swinging round a fixed line
        private const val WANDER_FREQUENCY = 0.1f
        private const val WANDER_SIZE = 0.6f

        // A streamer moving along its length this fast, in dp per 60 fps step, spins at its spin speed.
        // Faster it spins more, by the square root, up to MAX_SPIN_BOOST times as fast.
        private const val SPIN_SPEED_REFERENCE = 2f
        private const val MAX_SPIN_BOOST = 6f
    }

    private val ribbon = shape as? Shape.Ribbon

    // A streamer stays upright, so it doesn't point the way it's moving
    var rotation = if (faceDirection && ribbon == null) heading() else 0f

    /**
     * How many degrees the particle leans into its drift swing right now, on top of [rotation]
     */
    var tiltAngle = 0f
        private set

    // Where the particle is in its drift swing, and how far that has moved it sideways so far
    private var swayAngle = swayPhase
    private var harmonicAngle = harmonicPhase
    private var wanderAngle = wanderPhase
    private var swayOffset = 0f
    private var swayStrength = 0f

    // How far gravity may build up before the particle falls at fallSpeed, in dp per step per step
    private val maxFallAcceleration =
        if (fallSpeed > 0f && damping < 1f) fallSpeed / STEPS_PER_SECOND * (1f - damping) / damping else Float.MAX_VALUE

    // How far a streamer has turned around its own length
    private var spin = spinPhase

    // Past sway offsets of a streamer, newest at trailNewest, one every TRAIL_STEP seconds
    private val bend = ribbonBend ?: ribbon?.bend ?: 0f
    private val trail = if (ribbon != null && bend > 0f) FloatArray(TRAIL_SIZE) else null
    private var trailNewest = 0
    private var trailClock = 0f

    // The way a streamer's body runs from its head. It starts trailing behind the way it's launched,
    // so a streamer shot up doesn't fly tail first and then flip over
    private var hangX = 0f
    private var hangY = -1f

    // Which way a streamer tips over at the top of a straight up shot, from its own random start
    private val flopSide = if (spinPhase > PI) 1f else -1f

    init {
        if (ribbon != null) {
            val targetX = -velocity.x
            val targetY = -velocity.y - HANG
            val targetLength = sqrt(targetX * targetX + targetY * targetY)
            if (targetLength > 0f) {
                hangX = targetX / targetLength
                hangY = targetY / targetLength
            }
        }
    }

    private var rotationWidth = width

    private var gravity = Vector(0f, GRAVITY)

    var alpha: Int = 255

    // Keeps the part of a fade step that doesn't fit in the Int alpha, so it fades the same at 120 fps
    private var exactAlpha = 255f
    var scaleX = 0f

    /**
     * The color of the particle with the current alpha value applied
     */
    var alphaColor: Int = 0

    /**
     * Determines whether the particle should be drawn.
     * Set to false when no part of the particle is inside the view
     */
    var drawParticle = true
        private set

    /**
     * Returns the size of the particle in pixels
     */
    fun getSize(): Float = width

    /**
     * Checks if the particle is "dead", i.e., its alpha value has reached 0
     */
    fun isDead(): Boolean = alpha <= 0

    /**
     * Applies a force to the particle, which affects its acceleration
     */
    fun applyForce(force: Vector) {
        acceleration.addScaled(force, 1f / mass)
    }

    /**
     * Updates the state of the particle for each frame of the animation.
     */
    fun render(
        deltaTime: Float,
        drawArea: CoreRect,
    ) {
        val steps = deltaTime * STEPS_PER_SECOND
        render(deltaTime, drawArea, steps, damping.pow(steps))
    }

    /**
     * Same as [render], with the damping for this frame worked out by the caller.
     * PartySystem does that once per frame, since pow is costly to run for every piece.
     */
    internal fun render(
        deltaTime: Float,
        drawArea: CoreRect,
        steps: Float,
        stepDamping: Float,
    ) {
        // Drag takes part of gravity away so the particle floats down slower
        acceleration.addScaled(gravity, (1f - drag) * steps / mass)
        // Air holds a drifting piece back, so it settles at its own fall speed instead of speeding up forever
        if (acceleration.y > maxFallAcceleration) acceleration.y = maxFallAcceleration
        update(deltaTime, steps, stepDamping, drawArea)
    }

    /**
     * Updates the state of the particle based on its current acceleration, velocity, and location.
     * Also handles the fading out of the particle when its lifespan is over.
     */
    private fun update(
        deltaTime: Float,
        steps: Float,
        stepDamping: Float,
        drawArea: CoreRect,
    ) {
        // Below the view nothing comes back up, so the particle is dead once it has fully left
        if (centerY() - reach() > drawArea.y + drawArea.height) {
            alpha = 0
            return
        }

        velocity.addScaled(acceleration, steps)
        // Move as far as the 60 fps frames inside this step would add up to,
        // so damping slows confetti down the same at any frame rate
        val travel = if (damping == 1f) steps else damping * (1f - stepDamping) / (1f - damping)
        location.addScaled(velocity, travel * pixelDensity)
        velocity.mult(stepDamping)
        // Without damping there's nothing to settle against, so the fall speed is held directly
        if (fallSpeed > 0f && damping >= 1f) velocity.y = velocity.y.coerceAtMost(fallSpeed / STEPS_PER_SECOND)

        if (sway != 0f || tilt != 0f) drift(deltaTime)
        if (ribbon != null) {
            hang(deltaTime, steps)
            spin = (spin + TWO_PI * spinRate() * deltaTime) % TWO_PI
            recordTrail(deltaTime)
        }

        lifespan -= (deltaTime * MILLIS_IN_SECOND).toLong()
        if (lifespan <= 0) updateAlpha(steps)

        if (ribbon == null) {
            // 2D rotation around the center of the confetti, or the way it's moving
            if (faceDirection) {
                rotation = heading()
            } else {
                rotation += rotationSpeed2D * steps
                if (rotation >= FULL_CIRCLE) rotation = 0f
            }

            // 3D rotation effect by decreasing the width and make sure that rotationSpeed is always
            // positive by using abs
            rotationWidth -= abs(rotationSpeed3D) * steps
            if (rotationWidth < 0) rotationWidth = width

            scaleX = abs(rotationWidth / width - 0.5f) * 2
        } else {
            // A streamer stays upright and spins around its own length instead
            scaleX = 1f
        }
        alphaColor = (alpha shl 24) or (color and 0xffffff)

        drawParticle = isInside(drawArea)
    }

    // The angle the confetti is moving at in degrees, 0 is right and 90 is down like Party's angle
    private fun heading(): Float = (atan2(velocity.y, velocity.x) * DEGREES_PER_RADIAN).toFloat()

    /**
     * Swings the particle from side to side while it falls. The swing only depends on time,
     * so it looks the same at any frame rate. It grows as the particle turns downward,
     * so confetti shooting up or sideways isn't pulled off its path.
     */
    private fun drift(deltaTime: Float) {
        swayAngle = (swayAngle + TWO_PI * swayFrequency * deltaTime) % TWO_PI
        harmonicAngle = (harmonicAngle + TWO_PI * swayFrequency * HARMONIC_SPEED * deltaTime) % TWO_PI
        wanderAngle = (wanderAngle + TWO_PI * WANDER_FREQUENCY * deltaTime) % TWO_PI
        val speed = sqrt(velocity.x * velocity.x + velocity.y * velocity.y)
        val falling = if (speed == 0f) 0f else (velocity.y / speed).coerceIn(0f, 1f)
        // Grows and shrinks gradually, so turning downward doesn't throw the piece sideways
        swayStrength += (falling - swayStrength) * (1f - exp(-deltaTime / SWAY_EASE))
        val swing = (sin(swayAngle) + HARMONIC_SIZE * sin(harmonicAngle)) / (1 + HARMONIC_SIZE)
        val offset = sway * pixelDensity * swayStrength * (swing + WANDER_SIZE * sin(wanderAngle))
        location.x += offset - swayOffset
        swayOffset = offset
        // Lean into the way it's swinging, most where it swings fastest
        val swingSpeed = (cos(swayAngle) + HARMONIC_SIZE * HARMONIC_SPEED * cos(harmonicAngle)) / (1 + HARMONIC_SIZE * HARMONIC_SPEED)
        tiltAngle = tilt * swayStrength * swingSpeed
    }

    /**
     * How many turns a second a streamer spins now. It turns like a screw driven through the air the way it moves
     * along its body: its loops travel back toward its tail, so they hold roughly still in the air while the body slides
     * past. Falling, the loops seem to climb up the body; spinning the other way would look like it's screwing itself up.
     * It spins faster the faster it moves, and barely turns while it hangs still.
     */
    private fun spinRate(): Float {
        val alongBody = -(velocity.x * hangX + velocity.y * hangY)
        val boost = sqrt(abs(alongBody) / SPIN_SPEED_REFERENCE).coerceAtMost(MAX_SPIN_BOOST)
        return if (alongBody < 0f) spinSpeed * boost else -spinSpeed * boost
    }

    /**
     * Swings a streamer's body round to trail behind the way it moves, with gravity pulling it to hang up from
     * its head. Like a rope, it turns by how far it travels: quickly while it flies, and slowly at the top of
     * its arc where it hardly moves, so it flops over instead of flipping.
     */
    private fun hang(
        deltaTime: Float,
        steps: Float,
    ) {
        val targetX = -velocity.x
        val targetY = -velocity.y - HANG
        val targetLength = sqrt(targetX * targetX + targetY * targetY)
        if (targetLength == 0f) return
        val travelled = sqrt(velocity.x * velocity.x + velocity.y * velocity.y) * steps * pixelDensity
        val follow = travelled / (FOLLOW_DISTANCE * ribbonLength.coerceAtLeast(width))
        val ease = 1f - exp(-(follow + deltaTime / HANG_EASE))
        // Turn the short way round. Straight up or down there's no short way, so it tips to its own side.
        val current = atan2(hangY, hangX)
        var turn = atan2(targetY, targetX) - current
        while (turn > PI) turn -= TWO_PI
        while (turn < -PI) turn += TWO_PI
        if (abs(turn) > PI - TIP_OVER) turn = flopSide * abs(turn)
        // A body can only swing round so fast, so turning over takes a moment
        val angle = current + (turn * ease).coerceIn(-MAX_TURN * deltaTime, MAX_TURN * deltaTime)
        hangX = cos(angle)
        hangY = sin(angle)
    }

    // Keeps the sway offset every TRAIL_STEP, so it's the same at any frame rate
    private fun recordTrail(deltaTime: Float) {
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

    /**
     * How a [Shape.Ribbon] streamer is spinning and bending right now, or null for other shapes.
     * Its body hangs up from its head, pulled back against the way it moves, and each point along it
     * sits where the head's swing was a little earlier, so the tail follows the head.
     */
    internal fun ribbonBody(): RibbonBody? {
        val ribbon = ribbon ?: return null
        val offsets = FloatArray(BODY_POINTS)
        if (trail != null) {
            for (i in 1 until BODY_POINTS) {
                val delay = i / (BODY_POINTS - 1f) * bend * MAX_LAG
                offsets[i] = swayOffsetAgo(trail, delay) - swayOffset
            }
        }
        return RibbonBody(
            spin = spin,
            length = ribbonLength,
            loopSpacing = ribbon.loopSpacing * loopScale * pixelDensity,
            axisX = hangX,
            axisY = hangY,
            offsets = offsets,
            unevenness = shapePhases,
            thicknessScale = thicknessScale,
            contrast = contrast,
        )
    }

    private fun centerX() = location.x + width / 2

    private fun centerY() = location.y + width / 2

    // How far any part of the particle can reach from its center, whatever its rotation.
    // A streamer's body reaches its whole length from its head, in any direction it hangs.
    private fun reach() = if (ribbon != null) ribbonLength + width else width * HALF_DIAGONAL

    /**
     * True while any part of the particle overlaps the draw area.
     * Checking only the top-left point made particles pop in at the top and left edges.
     */
    private fun isInside(drawArea: CoreRect): Boolean {
        val reach = reach()
        val centerX = centerX()
        val centerY = centerY()
        return centerX + reach >= drawArea.x &&
            centerX - reach <= drawArea.x + drawArea.width &&
            centerY + reach >= drawArea.y &&
            centerY - reach <= drawArea.y + drawArea.height
    }

    private fun updateAlpha(steps: Float) {
        exactAlpha =
            if (fadeOut) {
                (exactAlpha - ALPHA_DECREMENT * steps).coerceAtLeast(0f)
            } else {
                0f
            }
        alpha = exactAlpha.toInt()
    }
}
