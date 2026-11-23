package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * How a piece of confetti sways from side to side while it falls, the way paper drifts down.
 * [Confetti] holds one when drift is on and moves the piece with it every frame.
 * Each piece gets its own values from [nl.dionsegijn.konfetti.core.Drift] when it's made.
 * Only Konfetti makes and reads them.
 */
internal class Swing(
    private val damping: Float,
    private val pixelDensity: Float,
    private val sway: Float = 0f,
    private val frequency: Float = 0f,
    private val tilt: Float = 0f,
    private val drag: Float = 0f,
    private val fallSpeed: Float = 0f,
    swayPhase: Float = 0f,
    harmonicPhase: Float = 0f,
    wanderPhase: Float = 0f,
) {
    private companion object {
        private const val TWO_PI = (2 * PI).toFloat()

        // Seconds the swing takes to grow once a piece falls, so it doesn't jump sideways
        private const val SWAY_EASE = 0.5f

        // A smaller, faster second swing, so no two swings are the same size
        private const val HARMONIC_SPEED = 2.3f
        private const val HARMONIC_SIZE = 0.35f

        // A slow wander to one side, so a piece drifts off instead of swinging round one line
        private const val WANDER_FREQUENCY = 0.1f
        private const val WANDER_SIZE = 0.6f
    }

    // Where the piece is in its swing
    private var swayAngle = swayPhase
    private var harmonicAngle = harmonicPhase
    private var wanderAngle = wanderPhase
    private var swayStrength = 0f

    /** How far the swing has moved the piece sideways so far */
    internal var swayOffset = 0f
        private set

    /** How many degrees the piece leans into its swing right now */
    internal var tiltAngle = 0f
        private set

    /** Drag takes part of gravity away so the piece floats down slower */
    internal val gravityFactor = 1f - drag

    // Damping slows a piece by (1 - damping) of its speed each step, so it stops speeding up
    // once gravity adds that much. This caps gravity so that happens at fallSpeed.
    private val maxFallAcceleration =
        if (fallSpeed > 0f && damping < 1f) fallSpeed / Confetti.STEPS_PER_SECOND * (1f - damping) / damping else Float.MAX_VALUE

    internal fun capFall(acceleration: Vector) {
        if (acceleration.y > maxFallAcceleration) acceleration.y = maxFallAcceleration
    }

    // Without damping there's nothing to settle against, so the fall speed is held directly
    internal fun holdFall(velocity: Vector) {
        if (fallSpeed > 0f && damping >= 1f) velocity.y = velocity.y.coerceAtMost(fallSpeed / Confetti.STEPS_PER_SECOND)
    }

    /**
     * Swings the piece from side to side once it falls, by time so it's the same at any frame rate.
     * [frequencyScale] slows the swing down for pieces that swing slower, like a long streamer.
     */
    internal fun update(
        location: Vector,
        velocity: Vector,
        deltaTime: Float,
        frequencyScale: Float,
    ) {
        if (sway == 0f && tilt == 0f) return
        val swingsPerSecond = frequency * frequencyScale
        swayAngle = (swayAngle + TWO_PI * swingsPerSecond * deltaTime) % TWO_PI
        harmonicAngle = (harmonicAngle + TWO_PI * swingsPerSecond * HARMONIC_SPEED * deltaTime) % TWO_PI
        wanderAngle = (wanderAngle + TWO_PI * WANDER_FREQUENCY * deltaTime) % TWO_PI
        val speed = sqrt(velocity.x * velocity.x + velocity.y * velocity.y)
        // Swings more the more it falls, so confetti shooting up or sideways stays on its path
        val falling = if (speed == 0f) 0f else (velocity.y / speed).coerceIn(0f, 1f)
        swayStrength += (falling - swayStrength) * (1f - exp(-deltaTime / SWAY_EASE))
        val swing = (sin(swayAngle) + HARMONIC_SIZE * sin(harmonicAngle)) / (1 + HARMONIC_SIZE)
        val offset = sway * pixelDensity * swayStrength * (swing + WANDER_SIZE * sin(wanderAngle))
        location.x += offset - swayOffset
        swayOffset = offset
        // Lean into the way it's swinging, most where it swings fastest
        val swingSpeed = (cos(swayAngle) + HARMONIC_SIZE * HARMONIC_SPEED * cos(harmonicAngle)) / (1 + HARMONIC_SIZE * HARMONIC_SPEED)
        tiltAngle = tilt * swayStrength * swingSpeed
    }
}
