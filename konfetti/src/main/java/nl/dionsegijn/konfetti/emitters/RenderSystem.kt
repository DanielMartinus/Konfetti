package nl.dionsegijn.konfetti.emitters

import android.graphics.Canvas
import nl.dionsegijn.konfetti.Confetti
import nl.dionsegijn.konfetti.models.ConfettiConfig
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import nl.dionsegijn.konfetti.modules.LocationModule
import nl.dionsegijn.konfetti.modules.VelocityModule
import java.util.Random

/**
 * Implementation class for rendering confetti
 * Implementations like [BurstEmitter] and [StreamEmitter] define at
 * what rate the confetti is created while RenderSystem is creating the confetti
 * and passing through the canvas to let the confetti render itself
 */
class RenderSystem(
    private val location: LocationModule,
    private val velocity: VelocityModule,
    private val sizes: Array<Size>,
    private val shapes: Array<Shape>,
    private val colors: IntArray,
    private val config: ConfettiConfig,
    private val emitter: Emitter
) {

    /**
     * Whether the render system is allowed to add more confetti
     */
    var enabled = true

    private val random = Random()
    private var gravity = Vector(0f, 0.01f)
    private val particles: MutableList<Confetti> = mutableListOf()

    init {
        emitter.addConfettiFunc = this::addConfetti
    }

    private fun addConfetti() {
        particles.add(Confetti(
                location = Vector(location.x, location.y),
                size = sizes[random.nextInt(sizes.size)],
                shape = shapes[random.nextInt(shapes.size)],
                color = colors[random.nextInt(colors.size)],
                lifespan = config.timeToLive,
                fadeOut = config.fadeOut,
                velocity = this.velocity.getVelocity(),
                rotate = config.rotate,
                maxAcceleration = velocity.maxAcceleration,
                accelerate = config.accelerate)
        )
    }

    fun render(canvas: Canvas, deltaTime: Float) {
        if (enabled) emitter.createConfetti(deltaTime)

        for (i in particles.size - 1 downTo 0) {
            val particle = particles[i]
            particle.applyForce(gravity)
            particle.render(canvas, deltaTime)
            if (particle.isDead()) particles.removeAt(i)
        }
    }

    fun getActiveParticles(): Int = particles.size

    fun isDoneEmitting(): Boolean =
        (emitter.isFinished() && particles.size == 0) || (!enabled && particles.size == 0)
}
