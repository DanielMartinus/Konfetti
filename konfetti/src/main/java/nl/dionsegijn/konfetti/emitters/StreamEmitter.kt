package nl.dionsegijn.konfetti.emitters

import nl.dionsegijn.konfetti.models.ConfettiConfig
import nl.dionsegijn.konfetti.models.LocationModule
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.modules.VelocityModule

/**
 * Created by dionsegijn on 4/2/17.
 */
class StreamEmitter(location: LocationModule, velocity: VelocityModule, sizes: Array<Size>, shapes: Array<Shape>, colors: IntArray, config: ConfettiConfig) : Emitter(location, velocity, sizes, shapes, colors, config) {

    /** Max amount of particles allowed to be created */
    private var maxParticles = -1
    /** Keeping count of how many particles are created */
    private var particlesCreated = 0
    /** Max time in milliseconds allowed to emit */
    private var emittingTime: Long = 0
    /** Elapsed time in milliseconds */
    private var elapsedTime: Float = 0f
    /** Milliseconds per particle creation */
    private var amountPerMs: Float = 0f
    /** Elapsed time in milliseconds */
    private var createParticleMs: Float = 0f

    fun emit(particlesPerSecond: Int, emittingTime: Long = 0L, maxParticles: Int = -1): StreamEmitter {
        this.maxParticles = maxParticles
        this.emittingTime = emittingTime
        amountPerMs = 1f / particlesPerSecond
        addConfetti()
        return this
    }

    /**
     * If timer isn't started yet, set initial start time
     * Create the first confetti immediately and update the last emitting time
     */
    override fun createConfetti(deltaTime: Float) {

        // If maxParticles is set and amount of particles created is not within the range of
        // maxParticles stop emitting
        if(maxParticles in 1..(particlesCreated - 1)) {
            return
        }

        // Check if particle should be created
        if(createParticleMs > amountPerMs && !isTimeElapsed()) {
            // Calculate how many particle  to create in the elapsed time
            val amount: Int = (createParticleMs / amountPerMs).toInt()
            (1..amount).forEach { this.addConfetti() }
            // Reset timer and add left over time for the next cycle
            createParticleMs %= amountPerMs
        }
        createParticleMs += deltaTime

        elapsedTime += deltaTime * 1000
    }

    private fun isTimeElapsed(): Boolean = if(emittingTime == 0L) false else elapsedTime > emittingTime

    /**
     * When time is up and all particles disappeared
     */
    override fun isDoneEmitting(): Boolean {
        return if(emittingTime > 0L) {
            elapsedTime >= emittingTime && particles.size == 0
        } else {
            particles.size == 0
        }
    }

    override fun addConfetti() {
        particlesCreated++
        super.addConfetti()
    }

}
