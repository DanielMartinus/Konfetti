package nl.dionsegijn.konfetti.core.emitter

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.abs

/**
 * Confetti holds all data to the current state of the particle
 * With every new frame render is called to update the properties in this class
 */
class Confetti(
    var id: Int,
    var location: Vector,
    private val color: Int,
    val size: Size,
    val shape: Shape,
    var lifespan: Long = -1L,
    val fadeOut: Boolean = true,
    private var acceleration: Vector = Vector(0f, 0f),
    var velocity: Vector = Vector(),
    var damping: Float,
    val rotationSpeed3D: Float = 1f,
    val rotationSpeed2D: Float = 1f,
) {

    private val density = Resources.getSystem().displayMetrics.density
    private val mass = size.mass
    var width = size.sizeInPx
    private val paint: Paint = Paint()

    var rotation = 0f
    private var rotationWidth = width

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

        velocity.add(acceleration)
        velocity.mult(damping)

        location.addScaled(velocity, deltaTime * speedF * density)

        if (lifespan <= 0) updateAlpha(deltaTime)
        else lifespan -= (deltaTime * 1000).toLong()

        // 2D rotation around the center of the confetti
        rotation += rotationSpeed2D * deltaTime * speedF
        if (rotation >= 360) rotation = 0f

        // 3D rotation effect by decreasing the width and make sure that rotationSpeed is always
        // positive by using abs
        rotationWidth -= abs(rotationSpeed3D) * deltaTime * speedF
        if (rotationWidth > width) Log.e("Konfetti", "rotationWidth ($rotationWidth) -= rotationSpeed3D ($rotationSpeed3D) * deltaTime ($deltaTime) * speedF ($speedF)")
        if (rotationWidth < 0) rotationWidth = width

        scaleX = abs(rotationWidth / width - 0.5f) * 2
        alphaColor = (alpha shl 24) or (color and 0xffffff)

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
