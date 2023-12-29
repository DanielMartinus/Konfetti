package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.models.CoreRect
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.abs

/**
 * Confetti holds all data to the current state of the particle
 * Each frame update triggers the `render` method, which recalculates the particle's properties based on its current state.
 *
 * @property location The current position of the particle as a Vector object that contains x and y coordinates.
 * @property color The color of the particle, represented as an integer. (AARRGGBB)
 * @property width The width of the particle in pixels.
 * @property mass The mass of the particle, affecting how forces like gravity influence it. A particle with more mass will move slower under the same force.
 * @property shape The geometric shape of the particle.
 * @property lifespan The duration the particle should exist for in milliseconds.
 * @property fadeOut If true, the particle will gradually become transparent over its lifespan.
 * @property acceleration The current acceleration of the particle.
 * @property velocity The current velocity of the particle.
 * @property damping A factor that reduces the particle's velocity over time, simulating air resistance. A higher damping value will slow down the particle faster.
 * @property rotationSpeed3D The speed at which the particle rotates.
 * @property rotationSpeed2D The speed at which the particle rotates in 2D space.
 * @property pixelDensity The pixel density of the device's screen. This is used to ensure that the particle's movement looks consistent across devices with different screen densities.
 */
class Confetti(
    var location: Vector,
    private val color: Int,
    val width: Float,
    private val mass: Float,
    val shape: Shape,
    var lifespan: Long = -1L,
    val fadeOut: Boolean = true,
    private var acceleration: Vector = Vector(0f, 0f),
    var velocity: Vector = Vector(),
    var damping: Float,
    val rotationSpeed3D: Float = 1f,
    val rotationSpeed2D: Float = 1f,
    val pixelDensity: Float,
) {
    companion object {
        private const val DEFAULT_FRAME_RATE = 60f
        private const val GRAVITY = 0.02f
        private const val ALPHA_DECREMENT = 5
        private const val MILLIS_IN_SECOND = 1000
        private const val FULL_CIRCLE = 360f
    }

    var rotation = 0f
    private var rotationWidth = width

    // Expected frame rate
    private var frameRate = DEFAULT_FRAME_RATE
    private var gravity = Vector(0f, GRAVITY)

    var alpha: Int = 255
    var scaleX = 0f

    /**
     * The color of the particle with the current alpha value applied
     */
    var alphaColor: Int = 0

    /**
     * Determines whether the particle should be drawn.
     * Set to false when the particle moves out of the view
     */
    var drawParticle = true
        private set

    /**
     * Returns the size of the particle in pixels
     */
    fun getSize(): Float = width

    /**
     * Checks if the particle is "dead", i.e., its alpha value has reached 0
     */
    fun isDead(): Boolean = alpha <= 0

    /**
     * Applies a force to the particle, which affects its acceleration
     */
    fun applyForce(force: Vector) {
        acceleration.addScaled(force, 1f / mass)
    }

    /**
     * Updates the state of the particle for each frame of the animation.
     */
    fun render(
        deltaTime: Float,
        drawArea: CoreRect,
    ) {
        applyForce(gravity)
        update(deltaTime, drawArea)
    }

    /**
     * Updates the state of the particle based on its current acceleration, velocity, and location.
     * Also handles the fading out of the particle when its lifespan is over.
     */
    private fun update(
        deltaTime: Float,
        drawArea: CoreRect,
    ) {
        // Calculate frameRate dynamically, fallback to 60fps in case deltaTime is 0
        frameRate = if (deltaTime > 0) 1f / deltaTime else DEFAULT_FRAME_RATE

        if (location.y > drawArea.height) {
            alpha = 0
            return
        }

        velocity.add(acceleration)
        velocity.mult(damping)

        location.addScaled(velocity, deltaTime * frameRate * pixelDensity)

        lifespan -= (deltaTime * MILLIS_IN_SECOND).toLong()
        if (lifespan <= 0) updateAlpha(deltaTime)

        // 2D rotation around the center of the confetti
        rotation += rotationSpeed2D * deltaTime * frameRate
        if (rotation >= FULL_CIRCLE) rotation = 0f

        // 3D rotation effect by decreasing the width and make sure that rotationSpeed is always
        // positive by using abs
        rotationWidth -= abs(rotationSpeed3D) * deltaTime * frameRate
        if (rotationWidth < 0) rotationWidth = width

        scaleX = abs(rotationWidth / width - 0.5f) * 2
        alphaColor = (alpha shl 24) or (color and 0xffffff)

        drawParticle = drawArea.contains(location.x.toInt(), location.y.toInt())
    }

    private fun updateAlpha(deltaTime: Float) {
        alpha =
            if (fadeOut) {
                val interval = ALPHA_DECREMENT * deltaTime * frameRate
                (alpha - interval.toInt()).coerceAtLeast(0)
            } else {
                0
            }
    }
}
