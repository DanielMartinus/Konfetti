package nl.dionsegijn.konfetti

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import java.util.*

class Confetti(var location: Vector,
               val color: Int,
               val size: Size,
               val shape: Shape,
               val lifespan: Long = 2000L,
               val fadeOut: Boolean = true,
               var acceleration: Vector = Vector(0f, 0f),
               var velocity: Vector = Vector()) {

    private val mass = size.mass
    private var width = size.size
    private val paint: Paint = Paint()

    private var rotationSpeed = 1f
    private var rotation = 0f
    private var rotationWidth = width

    private var createdAt: Long
    private var alpha: Int = 255

    init {
        paint.color = color
        rotationSpeed = 3 * Random().nextFloat() + 1
        createdAt = System.currentTimeMillis()
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

    fun render(canvas: Canvas, ms: Long) {
        update(ms)
        display(canvas)
    }

    fun update(ms: Long) {
        velocity.add(acceleration)
        location.add(velocity)

        if((ms - createdAt) >= lifespan) {
            if(fadeOut) alpha -= 5 else alpha = 0
        }

        rotation += rotationSpeed
        if (rotation >= 360) rotation = 0f

        rotationWidth -= rotationSpeed
        if (rotationWidth < 0) rotationWidth = width
    }

    fun display(canvas: Canvas) {
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
