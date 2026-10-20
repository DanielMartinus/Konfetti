package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.Drift
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.models.CoreRect
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Emitter is responsible for creating a certain amount of particles per tick.
 * - Creating x amount of particles in a certain time frame
 * - Creating x amount of particles until the threshold [maxParticles] is met
 */
class PartyEmitter(
    private val emitterConfig: EmitterConfig,
    private val pixelDensity: Float,
    private val random: Random = Random.Default,
) : BaseEmitter() {
    // Keeping count of how many particles are created whilst running the emitter
    private var particlesCreated = 0

    /** Elapsed time in milliseconds */
    private var elapsedTime: Float = 0f

    /** Amount of time elapsed since last particle creation in milliseconds */
    private var createParticleMs: Float = 0f

    /**
     * If timer isn't started yet, set initial start time
     * Create the first confetti immediately and update the last emitting time
     */
    override fun createConfetti(
        deltaTime: Float,
        party: Party,
        drawArea: CoreRect,
    ): List<Confetti> {
        createParticleMs += deltaTime

        // Initial deltaTime can't be higher than the emittingTime, if so calculate
        // amount of particles based on max emittingTime
        val emittingTime = emitterConfig.emittingTime / 1000f
        if (elapsedTime == 0f && deltaTime > emittingTime) {
            createParticleMs = emittingTime
        }

        val particles = mutableListOf<Confetti>()

        // Check if particle should be created. A non-positive interval is invalid
        // and used to divide into an unbounded burst, so it creates nothing.
        val amountPerMs = emitterConfig.amountPerMs
        if (amountPerMs > 0f && createParticleMs >= amountPerMs && !isTimeElapsed()) {
            val requested = (createParticleMs / amountPerMs).toInt()
            val amount = requested.coerceAtMost(remainingParticleAllowance())

            repeat(amount) {
                particles.add(createParticle(party, drawArea))
            }

            // Reset timer and add left over time for next cycle
            createParticleMs %= amountPerMs
        }

        elapsedTime += deltaTime * 1000
        return particles
    }

    /**
     * How many more particles [EmitterConfig.max] still allows.
     * [Emitter.perSecond] has no bound.
     */
    private fun remainingParticleAllowance(): Int {
        val maxParticles = emitterConfig.maxParticles
        if (maxParticles < 0) return Int.MAX_VALUE
        return (maxParticles - particlesCreated).coerceAtLeast(0)
    }

    /**
     * Create particle based on the [Party] configuration
     * @param party Configurations used for creating the initial Confetti states
     * @param drawArea the area and size of the canvas
     */
    private fun createParticle(
        party: Party,
        drawArea: CoreRect,
    ): Confetti {
        particlesCreated++
        with(party) {
            // Random values are drawn in the same order as before drift and ribbons existed,
            // so a seeded party without them plays exactly the same
            val randomSize = size[random.nextInt(size.size)]
            val location = position.get(drawArea).run { Vector(x, y) }
            val mass = randomSize.massWithVariance()
            val shape = getRandomShape(shapes)
            val color = colors[random.nextInt(colors.size)]
            val velocity = getVelocity()
            val rotationSpeed2D = rotation.rotationSpeed() * party.rotation.multiplier2D
            val rotationSpeed3D = rotation.rotationSpeed() * party.rotation.multiplier3D
            val drifts = drift.enabled
            val sway = if (drifts) drift.withVariance(drift.sway) else 0f
            val swayFrequency = if (drifts) drift.withVariance(drift.frequency) else 0f
            val swayPhase = if (drifts) randomAngle() else 0f
            // Each streamer gets its own length, and starts and spins its own way
            val ribbon = shape as? Shape.Ribbon
            val ribbonLength = if (ribbon != null) randomLength(ribbon) else 0f
            val spinPhase = if (ribbon != null) randomAngle() else 0f
            val spinSpeed = if (ribbon != null) randomSpin(ribbon) else 0f
            // Each drifting piece catches the air its own way, so they don't all fall at one speed
            val drag = if (drifts) drift.withVariance(drift.drag).coerceIn(0f, MAX_DRAG) else 0f
            val fallSpeed = if (drifts) drift.withVariance(FALL_SPEED * (1f - drag)) else 0f
            val harmonicPhase = if (drifts) randomAngle() else 0f
            val wanderPhase = if (drifts) randomAngle() else 0f
            // And no two streamers are curled quite the same
            val loopScale = if (ribbon != null) 1f + LOOP_SPREAD * (random.nextFloat() * 2f - 1f) else 1f
            val unevenness = if (ribbon != null) randomAngle() else 0f
            return Confetti(
                location = location,
                width = randomSize.sizeInDp * pixelDensity,
                mass = mass,
                shape = shape,
                color = color,
                lifespan = timeToLive,
                fadeOut = fadeOutEnabled,
                velocity = velocity,
                damping = party.damping,
                rotationSpeed2D = rotationSpeed2D,
                rotationSpeed3D = rotationSpeed3D,
                faceDirection = rotation.faceDirection,
                pixelDensity = pixelDensity,
                sway = sway,
                // A long streamer swings slower, like a longer pendulum
                swayFrequency = if (ribbon != null) swayFrequency * pendulum(ribbonLength) else swayFrequency,
                swayPhase = swayPhase,
                // A streamer doesn't tip into its swing, its body trailing behind its head is its lean
                tilt = if (drifts && ribbon == null) drift.tilt else 0f,
                drag = drag,
                ribbonLength = ribbonLength,
                spinPhase = spinPhase,
                spinSpeed = spinSpeed,
                fallSpeed = fallSpeed,
                harmonicPhase = harmonicPhase,
                wanderPhase = wanderPhase,
                loopScale = loopScale,
                shapePhases = unevenness,
            )
        }
    }

    /** [value] give or take [Drift.variance] of it */
    private fun Drift.withVariance(value: Float): Float = value + value * variance * (random.nextFloat() * 2f - 1f)

    private fun randomLength(ribbon: Shape.Ribbon): Float =
        (ribbon.minLength + (ribbon.maxLength - ribbon.minLength) * random.nextFloat()) * pixelDensity

    // How much slower a streamer of [length] pixels swings than flat confetti
    private fun pendulum(length: Float): Float = sqrt(PENDULUM_LENGTH * pixelDensity / length).coerceAtMost(1f)

    // Always the same way round, the direction comes from how it moves. Some spin faster than others.
    private fun randomSpin(ribbon: Shape.Ribbon): Float = ribbon.spin * (1f + SPIN_SPREAD * (random.nextFloat() * 2f - 1f))

    private fun randomAngle(): Float = random.nextFloat() * 2 * PI.toFloat()

    /**
     * Calculate a rotation speed multiplier based on the base and variance
     * @return rotation speed and return 0 when rotation is disabled
     */
    private fun Rotation.rotationSpeed(): Float {
        if (!enabled) return 0f
        val randomValue = random.nextFloat() * 2f - 1f
        return speed + (speed * variance * randomValue)
    }

    private fun Party.getSpeed(): Float =
        if (maxSpeed == -1f) {
            speed
        } else {
            ((maxSpeed - speed) * random.nextFloat()) + speed
        }

    /**
     * Get the mass with a slight variance added to create randomness between how each particle
     * will react in speed when moving up or down
     */
    private fun Size.massWithVariance(): Float = mass + (mass * (random.nextFloat() * massVariance))

    /**
     * Calculate velocity based on radian and speed
     * @return [Vector] velocity
     */
    private fun Party.getVelocity(): Vector {
        val speed = getSpeed()
        val radian = getAngle() * PI / 180
        val vx = speed * cos(radian).toFloat()
        val vy = speed * sin(radian).toFloat()
        return Vector(vx, vy)
    }

    private fun Party.getAngle(): Double {
        if (spread == 0) return angle.toDouble()

        val minAngle = angle - (spread / 2)
        val maxAngle = angle + (spread / 2)
        return (maxAngle - minAngle) * random.nextDouble() + minAngle
    }

    private fun Position.get(drawArea: CoreRect): Position.Absolute {
        return when (this) {
            is Position.Absolute -> Position.Absolute(x, y)
            is Position.Relative -> {
                Position.Absolute(
                    drawArea.width * x.toFloat(),
                    drawArea.height * y.toFloat(),
                )
            }
            is Position.Between -> {
                val minPos = min.get(drawArea)
                val maxPos = max.get(drawArea)
                return Position.Absolute(
                    x = random.nextFloat().times(maxPos.x.minus(minPos.x)) + minPos.x,
                    y = random.nextFloat().times(maxPos.y.minus(minPos.y)) + minPos.y,
                )
            }
        }
    }

    /**
     * Get a random shape from the list of shapes
     */
    private fun getRandomShape(shapes: List<Shape>): Shape {
        return shapes[random.nextInt(shapes.size)]
    }

    /**
     * If the [duration] is 0 it's not set and not relevant
     * If the emitting time is set check if [elapsedTime] exceeded the emittingTime
     */
    private fun isTimeElapsed(): Boolean {
        return when (emitterConfig.emittingTime) {
            0L -> false
            else -> elapsedTime >= emitterConfig.emittingTime
        }
    }

    override fun isFinished(): Boolean {
        return if (emitterConfig.emittingTime > 0L) {
            elapsedTime >= emitterConfig.emittingTime
        } else {
            false
        }
    }

    private companion object {
        // A streamer this long in dp swings as fast as Drift says, longer ones swing slower
        const val PENDULUM_LENGTH = 12f

        // How fast a drifting piece without drag settles falling, in dp a second
        const val FALL_SPEED = 220f

        // Drag stays below 1, so every piece still falls
        const val MAX_DRAG = 0.95f

        // How much a streamer's spin and loop spacing differ from its shape's, either way
        const val SPIN_SPREAD = 0.4f
        const val LOOP_SPREAD = 0.15f
    }
}
