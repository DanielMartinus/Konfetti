package nl.dionsegijn.konfetti

import android.graphics.Color
import nl.dionsegijn.konfetti.emitters.BurstEmitter
import nl.dionsegijn.konfetti.emitters.RenderSystem
import nl.dionsegijn.konfetti.emitters.StreamEmitter
import nl.dionsegijn.konfetti.models.ConfettiConfig
import nl.dionsegijn.konfetti.models.LocationModule
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.modules.VelocityModule
import java.util.*

/**
 * Created by dionsegijn on 3/26/17.
 */
class ParticleSystem(val konfettiView: KonfettiView) {

    private val random = Random()

    /** Modules */
    private var location = LocationModule(random)
    private var velocity = VelocityModule(random)

    /** Default values */
    private var colors = intArrayOf(Color.RED)
    private var sizes = arrayOf(Size(16))
    private var shapes = arrayOf(Shape.RECT)
    private var confettiConfig = ConfettiConfig()

    /**
     * Implementation of [BurstEmitter] or [StreamEmitter]
     * Render function of the renderSystem is directly accessed from [KonfettiView]
     */
    internal lateinit var renderSystem: RenderSystem

    /**
     * Set position to emit particles from
     */
    fun setPosition(x: Float, y: Float): ParticleSystem {
        location.setX(x)
        location.setY(y)
        return this
    }

    /**
     * Set position range to emit particles from
     * A random position on the x-axis between [minX] and [maxX] and y-axis between [minY] and [maxY]
     * will be picked for each confetti.
     * @param [maxX] leave this null to only emit from [minX]
     * @param [maxY] leave this null to only emit from [minY]
     */
    fun setPosition(minX: Float, maxX: Float? = null, minY: Float, maxY: Float? = null): ParticleSystem {
        location.betweenX(minX, maxX)
        location.betweenY(minY, maxY)
        return this
    }

    /**
     * One of the colors will be randomly picked when confetti is generated
     * Default color is Color.RED
     */
    fun addColors(vararg colors: Int): ParticleSystem {
        this.colors = colors
        return this
    }

    /**
     * Add one or more different sizes by defining a [Size] in dip and optionally its mass
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

    /**
     * Set if the confetti should fade out when its
     * time to live is expired
     */
    fun setFadeOutEnabled(fade: Boolean): ParticleSystem {
        confettiConfig.fadeOut = fade
        return this
    }

    /**
     * Time to live in milliseconds the confetti will either fade out or dissapear
     * This time to live is set for each confetti individually
     * Default is 2000ms, how higher the time to live how more it could impact the performance
     */
    fun setTimeToLive(timeInMs: Long): ParticleSystem {
        confettiConfig.timeToLive = timeInMs
        return this
    }

    /**
     * Burst will create and shoot all confetti at once
     * Calling this function will start the system rendering the confetti
     * [amount] - the amount of particles created at burst
     */
    fun burst(amount: Int) {
        val burstEmitter = BurstEmitter()
        renderSystem = RenderSystem(location, velocity, sizes, shapes, colors, confettiConfig, burstEmitter)
        burstEmitter.build(amount)
        start()
    }

    /**
     * Emit a certain amount of particles per second
     * calling this function will start the system rendering the confetti
     * [particlesPerSecond] amount of particles created per second
     * [emittingTime] max amount of time to emit in milliseconds
     */
    fun stream(particlesPerSecond: Int, emittingTime: Long) {
        val stream = StreamEmitter().build(particlesPerSecond = particlesPerSecond, emittingTime = emittingTime)
        renderSystem = RenderSystem(location, velocity, sizes, shapes, colors, confettiConfig, stream)
        start()
    }

    /**
     * Emit a certain amount of particles per second
     * calling this function will start the system rendering the confetti
     * [particlesPerSecond] amount of particles created per second
     * [maxParticles] max amount of particles to emit
     */
    fun stream(particlesPerSecond: Int, maxParticles: Int) {
        val stream = StreamEmitter().build(particlesPerSecond = particlesPerSecond, maxParticles = maxParticles)
        renderSystem = RenderSystem(location, velocity, sizes, shapes, colors, confettiConfig, stream)
        start()
    }

    fun doneEmitting(): Boolean = renderSystem.isDoneEmitting()

    /**
     * Add the system to KonfettiView. KonfettiView will initiate the rendering
     */
    private fun start() {
        konfettiView.start(this)
    }

    fun activeParticles(): Int {
        return renderSystem.getActiveParticles()
    }

}
