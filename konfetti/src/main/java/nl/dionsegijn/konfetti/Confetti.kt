package nl.dionsegijn.konfetti

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import kotlin.random.Random
import kotlin.math.abs

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
    val maxAcceleration: Float = -1f
) {

    private val mass = size.mass
    private var width = size.sizeInPx
    private val paint: Paint = Paint()

    private var rotationSpeed = 0f
    private var rotation = 0f
    private var rotationWidth = width

    // Expected frame rate
    private var speedF = 60f

    private var alpha: Int = 255

    init {
        val minRotationSpeed = 0.29f * Resources.getSystem().displayMetrics.density
        val maxRotationSpeed = minRotationSpeed * 3
        if (rotate) {
            rotationSpeed = maxRotationSpeed * Random.nextFloat() + minRotationSpeed
        }
        paint.color = color
    }

    private fun getSize(): Float = width

    fun isDead(): Boolean = alpha <= 0f

    fun applyForce(force: Vector) {
        val f = force.copy()
        f.div(mass)
        acceleration.add(f)
    }

    fun render(canvas: Canvas, deltaTime: Float) {
        update(deltaTime)
        display(canvas)
    }

    private fun update(deltaTime: Float) {
        if (accelerate && (acceleration.y < maxAcceleration || maxAcceleration == -1f)) {
            velocity.add(acceleration)
        }

        val v = velocity.copy()
        v.mult(deltaTime * speedF)
        location.add(v)

        if (lifespan <= 0) updateAlpha(deltaTime)
        else lifespan -= (deltaTime * 1000).toLong()

        val rSpeed = (rotationSpeed * deltaTime) * speedF
        rotation += rSpeed
        if (rotation >= 360) rotation = 0f

        rotationWidth -= rSpeed
        if (rotationWidth < 0) rotationWidth = width
    }

    private fun updateAlpha(deltaTime: Float) {
        if (fadeOut) {
            val interval = 5 * deltaTime * speedF
            if (alpha - interval < 0) alpha = 0
            else alpha -= (5 * deltaTime * speedF).toInt()
        } else {
            alpha = 0
        }
    }

    private fun display(canvas: Canvas) {
        // if the particle is outside the bottom of the view the lifetime is over.
        if (location.y > canvas.height) {
            lifespan = 0
            return
        }

        // Do not draw the particle if it's outside the canvas view
        if (location.x > canvas.width || location.x + getSize() < 0 || location.y + getSize() < 0) {
            return
        }

        paint.alpha = alpha

        val scaleX = abs(rotationWidth / width - 0.5f) * 2
        val centerX = scaleX * width / 2

        val saveCount = canvas.save()
        canvas.translate(location.x - centerX, location.y)
        canvas.rotate(rotation, centerX, width / 2)
        canvas.scale(scaleX, 1f)

        shape.draw(canvas, paint, width)
        canvas.restoreToCount(saveCount)
    }
}
