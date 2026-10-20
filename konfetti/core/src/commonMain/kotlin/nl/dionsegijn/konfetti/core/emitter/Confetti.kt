package nl.dionsegijn.konfetti.core.emitter

import nl.dionsegijn.konfetti.core.models.CoreRect
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow

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
 * @property faceDirection If true, the 2D rotation follows the way the particle is moving in place of [rotationSpeed2D].
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
    private val faceDirection: Boolean = false,
) {
    companion object {
        // Motion was tuned per frame at 60 fps, so one step is 1/60 of a second
        internal const val STEPS_PER_SECOND = 60f
        private const val GRAVITY = 0.02f
        private const val ALPHA_DECREMENT = 5
        private const val MILLIS_IN_SECOND = 1000
        private const val FULL_CIRCLE = 360f
        private const val DEGREES_PER_RADIAN = 180 / PI

        // Half the diagonal of a square, so the reach covers the piece at any rotation
        private const val HALF_DIAGONAL = 0.7072f
    }

    var rotation = if (faceDirection) heading() else 0f
    private var rotationWidth = width

    private var gravity = Vector(0f, GRAVITY)

    var alpha: Int = 255

    // Keeps the part of a fade step that doesn't fit in the Int alpha, so it fades the same at 120 fps
    private var exactAlpha = 255f
    var scaleX = 0f

    /**
     * The color of the particle with the current alpha value applied
     */
    var alphaColor: Int = 0

    /**
     * Determines whether the particle should be drawn.
     * Set to false when no part of the particle is inside the view
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
        val steps = deltaTime * STEPS_PER_SECOND
        render(deltaTime, drawArea, steps, damping.pow(steps))
    }

    /**
     * Same as [render], with the damping for this frame worked out by the caller.
     * PartySystem does that once per frame, since pow is costly to run for every piece.
     */
    internal fun render(
        deltaTime: Float,
        drawArea: CoreRect,
        steps: Float,
        stepDamping: Float,
    ) {
        acceleration.addScaled(gravity, steps / mass)
        update(deltaTime, steps, stepDamping, drawArea)
    }

    /**
     * Updates the state of the particle based on its current acceleration, velocity, and location.
     * Also handles the fading out of the particle when its lifespan is over.
     */
    private fun update(
        deltaTime: Float,
        steps: Float,
        stepDamping: Float,
        drawArea: CoreRect,
    ) {
        // Below the view nothing comes back up, so the particle is dead once it has fully left
        if (centerY() - reach() > drawArea.y + drawArea.height) {
            alpha = 0
            return
        }

        velocity.addScaled(acceleration, steps)
        // Move as far as the 60 fps frames inside this step would add up to,
        // so damping slows confetti down the same at any frame rate
        val travel = if (damping == 1f) steps else damping * (1f - stepDamping) / (1f - damping)
        location.addScaled(velocity, travel * pixelDensity)
        velocity.mult(stepDamping)

        lifespan -= (deltaTime * MILLIS_IN_SECOND).toLong()
        if (lifespan <= 0) updateAlpha(steps)

        // 2D rotation around the center of the confetti, or the way it's moving
        if (faceDirection) {
            rotation = heading()
        } else {
            rotation += rotationSpeed2D * steps
            if (rotation >= FULL_CIRCLE) rotation = 0f
        }

        // 3D rotation effect by decreasing the width and make sure that rotationSpeed is always
        // positive by using abs
        rotationWidth -= abs(rotationSpeed3D) * steps
        if (rotationWidth < 0) rotationWidth = width

        scaleX = abs(rotationWidth / width - 0.5f) * 2
        alphaColor = (alpha shl 24) or (color and 0xffffff)

        drawParticle = isInside(drawArea)
    }

    // The angle the confetti is moving at in degrees, 0 is right and 90 is down like Party's angle
    private fun heading(): Float = (atan2(velocity.y, velocity.x) * DEGREES_PER_RADIAN).toFloat()

    private fun centerX() = location.x + width / 2

    private fun centerY() = location.y + width / 2

    // How far any part of the particle can reach from its center, whatever its rotation
    private fun reach() = width * HALF_DIAGONAL

    /**
     * True while any part of the particle overlaps the draw area.
     * Checking only the top-left point made particles pop in at the top and left edges.
     */
    private fun isInside(drawArea: CoreRect): Boolean {
        val reach = reach()
        val centerX = centerX()
        val centerY = centerY()
        return centerX + reach >= drawArea.x &&
            centerX - reach <= drawArea.x + drawArea.width &&
            centerY + reach >= drawArea.y &&
            centerY - reach <= drawArea.y + drawArea.height
    }

    private fun updateAlpha(steps: Float) {
        exactAlpha =
            if (fadeOut) {
                (exactAlpha - ALPHA_DECREMENT * steps).coerceAtLeast(0f)
            } else {
                0f
            }
        alpha = exactAlpha.toInt()
    }
}
