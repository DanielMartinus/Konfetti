package nl.dionsegijn.konfetti.emitters

import android.graphics.Canvas
import nl.dionsegijn.konfetti.Confetti
import nl.dionsegijn.konfetti.models.*
import nl.dionsegijn.konfetti.models.Vector
import nl.dionsegijn.konfetti.modules.VelocityModule
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Implementation class for rendering confetti
 * Implementations like [BurstEmitter] and [StreamEmitter] define at
 * what rate the confetti is created while Emitter is creating the confetti
 * and passing through the canvas to let the confetti render itself
 */
abstract class Emitter(val location: LocationModule,
                       val velocity: VelocityModule,
                       val sizes: Array<Size>,
                       val shapes: Array<Shape>,
                       val colors: IntArray,
                       val config: ConfettiConfig) {

    private val random = Random()
    private var gravity = Vector(0f, 0.01f)
    internal val particles: MutableList<Confetti> = mutableListOf()

    abstract fun createConfetti(deltaTime: Float)
    abstract fun isDoneEmitting(): Boolean

    open fun addConfetti() {
        particles.add(Confetti(
                location = Vector(location.x, location.y),
                size = sizes[random.nextInt(sizes.size)],
                shape = shapes[random.nextInt(shapes.size)],
                color = colors[random.nextInt(colors.size)],
                lifespan = TimeUnit.MILLISECONDS.toNanos(config.timeToLive),
                fadeOut = config.fadeOut,
                velocity = this.velocity.getVelocity())
        )
    }

    fun render(canvas: Canvas, deltaTime: Float) {
        createConfetti(deltaTime)

        for(i in particles.size-1 downTo 0) {
            val particle = particles[i]
            particle.applyForce(gravity)
            particle.render(canvas, deltaTime)
            if(particle.isDead()) particles.removeAt(i)
        }
    }
}
