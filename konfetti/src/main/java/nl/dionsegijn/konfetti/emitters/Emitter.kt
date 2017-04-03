package nl.dionsegijn.konfetti.emitters

import android.graphics.Canvas
import nl.dionsegijn.konfetti.Confetti
import nl.dionsegijn.konfetti.models.*
import nl.dionsegijn.konfetti.models.Vector
import nl.dionsegijn.konfetti.modules.VelocityModule
import java.util.*

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
    private val particles: MutableList<Confetti> = mutableListOf()

    abstract fun createConfetti()

    open fun addConfetti() {
        val accY = random.nextInt(5) / 100f
        particles.add(Confetti(
                location = Vector(location.x, location.y),
                size = sizes[random.nextInt(sizes.size)],
                shape = shapes[random.nextInt(shapes.size)],
                color = colors[random.nextInt(colors.size)],
                lifespan = config.timeToLive,
                fadeOut = config.fadeOut,
                velocity = this.velocity.getVelocity(),
                acceleration = Vector(0f, accY))
        )
    }

    fun render(canvas: Canvas) {
        createConfetti()
        val it = particles.iterator()
        val currentMs = System.currentTimeMillis()
        while (it.hasNext()) {
            val c = it.next()
            c.applyForce(gravity)
            c.render(canvas, currentMs)
            if (c.isDead()) it.remove()
        }
    }
}