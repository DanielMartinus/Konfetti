package nl.dionsegijn.konfetti.core.emitter

import android.graphics.Rect
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.core.models.Vector
import java.lang.Math.toRadians
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Emitter is responsible for creating a certain amount of particles per tick.
 * - Creating x amount of particles in a certain time frame
 * - Creating x amount of particles until the threshold [maxParticles] is met
 */
class PartyEmitter(
    private val emitterConfig: EmitterConfig,
    private val pixelDensity: Float,
    private val random: Random = Random()
) : BaseEmitter() {

    /* Keeping count of how many particles are created whilst running the emitter */
    private var particlesCreated = 0

    /** Elapsed time in milliseconds */
    private var elapsedTime: Float = 0f

    /** Amount of time elapsed since last particle creation in milliseconds */
    private var createParticleMs: Float = 0f

    /**
     * If timer isn't started yet, set initial start time
     * Create the first confetti immediately and update the last emitting time
     */
    override fun createConfetti(deltaTime: Float, party: Party, drawArea: Rect): List<Confetti> {
        createParticleMs += deltaTime

        // Initial deltaTime can't be higher than the emittingTime, if so calculate
        // amount of particles based on max emittingTime
        val emittingTime = emitterConfig.emittingTime / 1000f
        if (elapsedTime == 0f && deltaTime > emittingTime) {
            createParticleMs = emittingTime
        }

        var particles = listOf<Confetti>()

        // Check if particle should be created
        if (createParticleMs >= emitterConfig.amountPerMs && !isTimeElapsed()) {
            // Calculate how many particle  to create in the elapsed time
            val amount: Int = (createParticleMs / emitterConfig.amountPerMs).toInt()

            particles = (1..amount).map { createParticle(party, drawArea) }

            // Reset timer and add left over time for next cycle
            createParticleMs %= emitterConfig.amountPerMs
        }

        elapsedTime += deltaTime * 1000
        return particles
    }

    /**
     * Create particle based on the [Party] configuration
     * @param party Configurations used for creating the initial Confetti states
     * @param drawArea the area and size of the canvas
     */
    private fun createParticle(party: Party, drawArea: Rect): Confetti {
        particlesCreated++
        with(party) {
            val randomSize = size[random.nextInt(size.size)]
            return Confetti(
                location = position.get(drawArea).run { Vector(x, y) },
                width = randomSize.sizeInDp * pixelDensity,
                mass = randomSize.massWithVariance(),
                shape = getRandomShape(party.shapes),
                color = colors[random.nextInt(colors.size)],
                lifespan = timeToLive,
                fadeOut = fadeOutEnabled,
                velocity = getVelocity(),
                damping = party.damping,
                rotationSpeed2D = rotation.rotationSpeed() * party.rotation.multiplier2D,
                rotationSpeed3D = rotation.rotationSpeed() * party.rotation.multiplier3D,
                pixelDensity = pixelDensity
            )
        }
    }

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
        if (maxSpeed == -1f) speed
        else ((maxSpeed - speed) * random.nextFloat()) + speed

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
        val radian = toRadians(getAngle())
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

    private fun Position.get(drawArea: Rect): Position.Absolute {
        return when (this) {
            is Position.Absolute -> Position.Absolute(x, y)
            is Position.Relative -> {
                Position.Absolute(
                    drawArea.width() * x.toFloat(),
                    drawArea.height() * y.toFloat()
                )
            }
            is Position.between -> {
                val minPos = min.get(drawArea)
                val maxPos = max.get(drawArea)
                return Position.Absolute(
                    x = random.nextFloat().times(maxPos.x.minus(minPos.x)) + minPos.x,
                    y = random.nextFloat().times(maxPos.y.minus(minPos.y)) + minPos.y
                )
            }
        }
    }

    /**
     * When the shape is a DrawableShape, mutate the drawable so that all drawables
     * have different values when drawn on the canvas.
     */
    private fun getRandomShape(shapes: List<Shape>): Shape {
        return when (val shape = shapes[random.nextInt(shapes.size)]) {
            is Shape.DrawableShape -> {
                val mutatedState =
                    shape.drawable.constantState?.newDrawable()?.mutate() ?: shape.drawable
                shape.copy(drawable = mutatedState)
            }
            else -> shape
        }
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
        } else false
    }
}
