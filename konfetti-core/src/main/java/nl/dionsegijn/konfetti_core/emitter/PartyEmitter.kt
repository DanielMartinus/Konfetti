package nl.dionsegijn.konfetti_core.emitter

import android.graphics.Rect
import nl.dionsegijn.konfetti_core.NewEmitter.EmitterConfig
import nl.dionsegijn.konfetti_core.Party
import nl.dionsegijn.konfetti_core.Position
import nl.dionsegijn.konfetti_core.Rotation
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Vector
import java.lang.Math.toRadians
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Stream emitter is a little more complex. It has several configurations for example:
 * - Creating x amount of particles in a certain time frame
 * - Creating x amount of particles until the threshold [maxParticles] is met
 */
class PartyEmitter(private val emitterConfig: EmitterConfig) : BaseEmitter() {

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

        var particles = listOf<Confetti>()

        // Check if particle should be created
        if (createParticleMs >= emitterConfig.amountPerMs && !isTimeElapsed()) {
            // Calculate how many particle  to create in the elapsed time
            val amount: Int = (createParticleMs / emitterConfig.amountPerMs).toInt()

            // TODO return created particles
            particles = (1..amount).map { createParticle(party, drawArea) }

            // Reset timer and add left over time for next cycle
            createParticleMs %= emitterConfig.amountPerMs
        }

        elapsedTime += deltaTime * 1000
        return particles
    }

    private val random = Random()

    /**
     * Create particle based on the Party configuration
     */
    private fun createParticle(party: Party, drawArea: Rect): Confetti {
        particlesCreated++
        with(party) {
            return Confetti(
                // TODO base location on min and max
                location = getPosition(drawArea).run { Vector(x, y) },
                size = size[random.nextInt(size.size)],
                shape = getRandomShape(party.shapes),
                color = colors[random.nextInt(colors.size)],
                lifespan = timeToLive,
                fadeOut = fadeOutEnabled,
                velocity = getVelocity(),
                rotate = party.rotation.enabled,
                maxAcceleration = maxAcceleration,
                accelerate = party.accelerationEnabled,
                rotationSpeedMultiplier = rotation.getRotationSpeedMultiplier(),
                speedDensityIndependent = speedDensityIndependent
            )
        }
    }

    /**
     * Calculate a rotation speed multiplier based on the base and variance
     * @return float multiplier
     */
    private fun Rotation.getRotationSpeedMultiplier(): Float {
        val randomValue = random.nextFloat() * 2f - 1f
        return baseRotationMultiplier + (baseRotationMultiplier * rotationVariance * randomValue)
    }

    /**
     * Calculate velocity based on radian and speed
     * @return [Vector] velocity
     */
    private fun Party.getVelocity(): Vector {
        val speed = startVelocity // TODO randomize the start speed
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

    private fun Party.getPosition(drawArea: Rect): Position.xy {
        return when (position) {
            is Position.xy -> Position.xy(position.x * 0.5f, position.y)
            is Position.relative -> {
                // TODO test drawArea where it has an offset of X pixels from the left. Right now it only takes a fullscreen canvas
                // TODO but should also work with a canvas having an offset (width + left?)
                Position.xy(
                    drawArea.width() * position.x.toFloat(),
                    drawArea.height() * position.y.toFloat()
                )
            }
            is Position.between -> TODO()
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
            EmitterConfig.INDEFINITE -> false
            else -> elapsedTime >= emitterConfig.emittingTime
        }
    }

    /**
     * TODO write new documentation
     */
    override fun isFinished(): Boolean {
        return when {
            emitterConfig.emittingTime > 0L -> {
                elapsedTime >= emitterConfig.emittingTime
            }
            emitterConfig.emittingTime == EmitterConfig.INDEFINITE -> false
            else -> false
        }
    }
}
