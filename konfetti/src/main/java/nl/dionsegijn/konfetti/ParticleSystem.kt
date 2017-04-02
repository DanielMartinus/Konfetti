package nl.dionsegijn.konfetti

import android.graphics.Canvas
import android.graphics.Color
import android.support.annotation.ColorInt
import nl.dionsegijn.konfetti.models.LocationModule
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import nl.dionsegijn.konfetti.modules.TimerModule
import nl.dionsegijn.konfetti.modules.VelocityModule
import java.util.*

/**
 * Created by dionsegijn on 3/26/17.
 */
class ParticleSystem(val renderer: KonfettiView) {

    private val random = Random()
    private var location = LocationModule(random)
    private var velocity = VelocityModule(random)
    private var timer = TimerModule()

    /** Default values */
    private var gravity = Vector(0f, 0.01f)
    private var colors = intArrayOf(Color.RED)
    private var sizes = arrayOf(Size.SMALL)
    private var shapes = arrayOf(Shape.RECT)

    private val particles: MutableList<Confetti> = mutableListOf()
    private var maxParticles = -1
    private var particlesCreated = 0

    private var emittingTime: Int = 0

    /**
     * One of the colors will be randomly picked when confetti is generated
     * Default color is Color.RED
     */
    fun addColors(@ColorInt vararg colors: Int): ParticleSystem {
        this.colors = colors
        return this
    }

    /**
     * Configure one or more sizes predefined in [Size].
     * Default size is [Size.SMALL]
     */
    fun addSizes(vararg possibleSizes: Size): ParticleSystem {
        this.sizes = possibleSizes.filterIsInstance<Size>().toTypedArray()
        return this
    }

    /**
     * Configure one or more shapes predefined in [Shape]
     * Default shape is [Shape.RECT] rectangle
     */
    fun addShapes(vararg shapes: Shape): ParticleSystem {
        this.shapes = shapes.filterIsInstance<Shape>().toTypedArray()
        return this
    }

    /**
     * Set direction you want to have the particles shoot to
     * [direction] direction in degrees ranging from 0 - 360
     * default direction is 0
     */
    fun setDirection(direction: Double): ParticleSystem {
        velocity.minAngle = Math.toRadians(direction)
        return this
    }

    /**
     * Set direction you want to have the particles shoot to
     * [minDirection] direction in degrees
     * [maxDirection] direction in degrees
     * Default minDirection is 0 and maxDirection is by default not set
     */
    fun setDirection(minDirection: Double, maxDirection: Double): ParticleSystem {
        velocity.minAngle = Math.toRadians(minDirection)
        velocity.maxAngle = Math.toRadians(maxDirection)
        return this
    }

    /**
     * Set the speed of the particle
     * If value is negative it will be automatically set to 0
     */
    fun setSpeed(speed: Float): ParticleSystem {
        velocity.minSpeed = speed
        return this
    }

    /**
     * Set the speed range of the particle
     * If one of the values is negative it will be automatically set to 0
     */
    fun setSpeed(minSpeed: Float, maxSpeed: Float): ParticleSystem {
        velocity.minSpeed = minSpeed
        velocity.maxSpeed = maxSpeed
        return this
    }



    fun fromPoint(x: Float, y: Float): ParticleSystem {
        location.setX(x)
        location.setY(y)
        return this
    }

    fun setMaxParticles(maxParticles: Int): ParticleSystem {
        this.maxParticles = maxParticles
        return this
    }

    fun betweenPoints(x1: Float, x2: Float, y1: Float, y2: Float): ParticleSystem {
        location.betweenX(x1, x2)
        location.betweenY(y1, y2)
        return this
    }

    /** ms per particle creation */
    var amountps: Double = 0.0

    fun emit(particlesPerSecond: Int, emittingTime: Int) {
        this.emittingTime = emittingTime
        amountps = 1000.0 / particlesPerSecond
        start()
    }

    var amountOfParticles = 0
    fun burst(amountOfParticles: Int) {
        this.amountOfParticles = amountOfParticles
        for (i in 1..amountOfParticles) {
            addConfetti()
        }
        start()
    }

    fun start() {
        renderer.start(this)
        timer.start()
        addConfetti()
    }

    fun createConfetti() {

        if (amountOfParticles > 0) {
            return
        }

        val elapsedTime = timer.getElapsedTimeLastEmit()

        // Check if particle should be created
        if (elapsedTime >= amountps && timer.getElapsedTimeFromStart() < emittingTime) {
            // could be that invalidate is slower than amount that should be created.
            // latency of 15 ms but need 1 particle per 3 ms = 5 particles
            // danger is that elapsed time will increase when more particles are on screen
            // will could grow forever and becomes too heavy.
            var newP = Math.floor(elapsedTime / amountps).toInt()
            if (newP > 3) newP = 3


//            Log.e("Konfetti", "Particles per second: " + newP + " amount alive: " + particles.size)
            for (i in 1..newP) {
                addConfetti()
                timer.updateEmitTime()
                particlesCreated++
            }
        }
    }

    fun addConfetti() {
        val accY = random.nextInt(5) / 100f
        particles.add(Confetti(
                location = Vector(location.x, location.y),
                size = sizes[random.nextInt(sizes.size)],
                shape = shapes[random.nextInt(shapes.size)],
                color = colors[random.nextInt(colors.size)],
                velocity = this.velocity.getVelocity(),
                acceleration = Vector(0f, accY))
        )
    }

    fun render(canvas: Canvas) {
        createConfetti()
        val it = particles.iterator()
        while (it.hasNext()) {
            val c = it.next()
            c.applyForce(gravity)
            c.render(canvas)
            if (c.isDead()) it.remove()
        }
    }

}
