package nl.dionsegijn.konfetti

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import java.util.*
import java.util.concurrent.TimeUnit

class Confetti(var location: Vector,
               val color: Int,
               val size: Size,
               val shape: Shape,
               var lifespan: Long = TimeUnit.SECONDS.toNanos(0.5.toLong()),
               val fadeOut: Boolean = true,
               var acceleration: Vector = Vector(0f, 0f),
               var velocity: Vector = Vector()) {

    private val mass = size.mass
    private var width = size.size
    private val paint: Paint = Paint()

    private var rotationSpeed = 1f
    private var rotation = 0f
    private var rotationWidth = width

    // Expected frame rate
    private var speedF = 60f

    private var alpha: Int = 255

    init {
        paint.color = color
        rotationSpeed = 3 * Random().nextFloat() + 1
    }

    fun getSize(): Float {
        return width
    }

    fun isDead(): Boolean {
        return alpha <= 0f
    }

    fun applyForce(force: Vector) {
        val f = force.copy()
        f.div(mass)
        acceleration.add(f)
    }

    fun render(canvas: Canvas, deltaTime: Float) {
        update(deltaTime)
        display(canvas)
    }

    fun update(deltaTime: Float) {
        val acc = acceleration.copy()
        acc.mult(deltaTime)
        acc.pow(2.0)

        velocity.add(acceleration)

        val v = velocity.copy()
        v.mult(deltaTime * speedF)

        location.add(v)

        val rSpeed = (rotationSpeed * deltaTime) * speedF
        rotation += rSpeed
        if (rotation >= 360) rotation = 0f

        rotationWidth -= rSpeed
        if (rotationWidth < 0) rotationWidth = width
    }

    fun display(canvas: Canvas) {
        // Do not draw the particle if it's outside the canvas view
        if (location.x > canvas.width || location.x + getSize() < 0
                || location.y > canvas.height || location.y + getSize() < 0) {
            return
        }

        val rect: RectF = RectF(
                location.x + (width - rotationWidth), // center of rotation
                location.y,
                location.x + rotationWidth,
                location.y + getSize())

        paint.alpha = alpha

        canvas.save()
        canvas.rotate(rotation, rect.centerX(), rect.centerY())
        when (shape) {
            Shape.CIRCLE -> canvas.drawOval(rect, paint)
            Shape.RECT -> canvas.drawRect(rect, paint)
        }
        canvas.restore()
    }

}
