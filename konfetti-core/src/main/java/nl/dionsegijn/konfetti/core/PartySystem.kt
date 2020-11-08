package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import java.util.Random

class PartySystem(
    private val config: Configuration,
    private val random: Random = Random()
) {

    private val velocityModule: VelocityModule2 by lazy {
        with(config) {
            VelocityModule2(
                minAngle,
                maxAngle,
                minSpeed,
                maxSpeed,
                maxAcceleration,
                baseRotationMultiplier,
                rotationVariance
            )
        }
    }

    fun createConfetti(): Confetti {
        with(config) {
            return Confetti(
                location = Vector(x, y),
                size = sizes[random.nextInt(sizes.size)],
                shape = getRandomShape(),
                color = colors[random.nextInt(colors.size)],
                lifespan = config.timeToLive,
                fadeOut = config.fadeOut,
                velocity = velocityModule.getVelocity(),
                rotate = config.rotate,
                maxAcceleration = velocityModule.maxAcceleration,
                accelerate = config.accelerate,
                rotationSpeedMultiplier = velocityModule.getRotationSpeedMultiplier()
            )
        }
    }

    private val x: Float
        get() {
            return if (config.maxX == null) {
                config.minX
            } else {
                random.nextFloat().times(config.maxX!!.minus(config.minX)) + config.minX
            }
        }

    private val y: Float
        get() {
            return if (config.maxY == null) {
                config.minY
            } else {
                random.nextFloat().times(config.maxY!!.minus(config.minY)) + config.minY
            }
        }

    /**
     * When the shape is a DrawableShape, mutate the drawable so that all drawables
     * have different values when drawn on the canvas.
     */
    private fun getRandomShape(): Shape {
        return when (val shape = config.shapes[random.nextInt(config.shapes.size)]) {
            is Shape.DrawableShape -> {
                val mutatedState =
                    shape.drawable.constantState?.newDrawable()?.mutate() ?: shape.drawable
                shape.copy(drawable = mutatedState)
            }
            else -> shape
        }
    }
}
