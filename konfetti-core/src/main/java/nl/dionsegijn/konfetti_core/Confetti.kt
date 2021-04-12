package nl.dionsegijn.konfetti_core

import android.content.res.Resources
import android.graphics.Paint
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size
import nl.dionsegijn.konfetti_core.models.Vector
import kotlin.random.Random

class Confetti(
    var location: Vector,
    val color: Int,
    val size: Size,
    val shape: Shape,
    var lifespan: Long = -1L,
    val fadeOut: Boolean = true,
    private var acceleration: Vector = Vector(0f, 0f),
    var velocity: Vector = Vector(),
    val rotate: Boolean = true,
    val accelerate: Boolean = true,
    val maxAcceleration: Float = -1f,
    val rotationSpeedMultiplier: Float = 1f,
    val speedDensityIndependent: Boolean = true
) {

    val density = Resources.getSystem().displayMetrics.density
    val mass = size.mass
    var width = size.sizeInPx
    val paint: Paint = Paint()

    var rotationSpeed = 0f
    var rotation = 0f
    var rotationWidth = width

    // Expected frame rate
    var speedF = 60f

    var alpha: Int = 255

    init {
        val minRotationSpeed = 0.29f * density
        val maxRotationSpeed = minRotationSpeed * 3
        if (rotate) {
            rotationSpeed = (maxRotationSpeed * Random.nextFloat() + minRotationSpeed) * rotationSpeedMultiplier
        }
        paint.color = color
    }

    fun getSize(): Float = width

    fun isDead(): Boolean = alpha <= 0

    fun applyForce(force: Vector) {
        acceleration.addScaled(force, 1f / mass)
    }

    fun render(deltaTime: Float) {
        update(deltaTime)
    }

    private fun update(deltaTime: Float) {
        if (accelerate && (acceleration.y < maxAcceleration || maxAcceleration == -1f)) {
            velocity.add(acceleration)
        }

        if (speedDensityIndependent) {
            location.addScaled(velocity, deltaTime * speedF * density)
        } else {
            location.addScaled(velocity, deltaTime * speedF)
        }

        if (lifespan <= 0) updateAlpha(deltaTime)
        else lifespan -= (deltaTime * 1000).toLong()

        val rSpeed = (rotationSpeed * deltaTime) * speedF
        rotation += rSpeed
        if (rotation >= 360) rotation = 0f

        rotationWidth -= rSpeed
        if (rotationWidth < 0) rotationWidth = width
    }

    private fun updateAlpha(deltaTime: Float) {
        alpha = if (fadeOut) {
            val interval = 5 * deltaTime * speedF
            (alpha - interval.toInt()).coerceAtLeast(0)
        } else {
            0
        }
    }
}
