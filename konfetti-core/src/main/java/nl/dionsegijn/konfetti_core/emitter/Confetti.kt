package nl.dionsegijn.konfetti_core.emitter

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size
import nl.dionsegijn.konfetti_core.models.Vector
import kotlin.math.abs
import kotlin.random.Random

/**
 * Confetti holds all data to the current state of the particle
 * With every new frame render is called to update the properties in this class
 */
class Confetti(
    var location: Vector,
    private val color: Int,
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

    private val density = Resources.getSystem().displayMetrics.density
    private val mass = size.mass
    var width = size.sizeInPx
    val paint: Paint = Paint()

    private var rotationSpeed = 0f
    var rotation = 0f
    var rotationWidth = width

    // Expected frame rate
    private var speedF = 60f

    var alpha: Int = 255
    var scaleX = 0f

    /**
     * Updated color with alpha values, later move to having one color
     */
    var alphaColor: Int = 0

    /**
     * If a particle moves out of the view we keep calculating its position but do not draw them
     */
    var drawParticle = true
        private set

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

    fun render(deltaTime: Float, drawArea: Rect) {
        update(deltaTime, drawArea)
    }

    private fun update(deltaTime: Float, drawArea: Rect) {
        if (location.y > drawArea.height()) {
            alpha = 0
            return
        }

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

        scaleX = abs(rotationWidth / width - 0.5f) * 2
        alphaColor = (alpha shl 24) or (color and 0xffffff)

        drawArea.inset(-width.toInt(), -width.toInt())
        drawParticle = drawArea.contains(location.x.toInt(), location.y.toInt())
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
