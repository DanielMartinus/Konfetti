package nl.dionsegijn.konfetti.emitters

import nl.dionsegijn.konfetti.models.ConfettiConfig
import nl.dionsegijn.konfetti.models.LocationModule
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.modules.TimerModule
import nl.dionsegijn.konfetti.modules.VelocityModule
import java.util.concurrent.TimeUnit

/**
 * Created by dionsegijn on 4/2/17.
 */
class StreamEmitter(timer: TimerModule, location: LocationModule, velocity: VelocityModule, sizes: Array<Size>, shapes: Array<Shape>, colors: IntArray, config: ConfettiConfig) : Emitter(timer, location, velocity, sizes, shapes, colors, config) {

    /** Max amount of particles allowed to be created */
    private var maxParticles = -1
    /** Keeping count of how many particles are created */
    private var particlesCreated = 0
    /** Max time in milliseconds allowed to emit */
    internal var emittingTime: Long = 0
    /** Milliseconds per particle creation */
    var amountPerMs: Long = 0L

    fun emit(particlesPerSecond: Int, emittingTime: Long = 0L, maxParticles: Int = -1): StreamEmitter {
        this.maxParticles = maxParticles
        this.emittingTime = emittingTime
        amountPerMs = TimeUnit.NANOSECONDS.toMillis(1000L / particlesPerSecond)
        return this
    }

    /**
     * If timer isn't started yet, set initial start time
     * Create the first confetti immediately and update the last emit time
     */
    override fun createConfetti() {
        if (!timer.isStarted()) {
            timer.start(emittingTime)
            this.addConfetti()
        }

        // if maxParticles is set and particlesCreated not within
        // range of maxParticles stop emitting
        if(maxParticles in 1..(particlesCreated - 1)) {
            return
        }

        val elapsedTime = timer.getElapsedTimeLastEmit()

        // Check if particle should be created
        if (elapsedTime >= amountPerMs && !timer.isMaxTime()) {
            this.addConfetti()
        }
    }

    /**
     * When time is up and all particles disappeared
     */
    override fun isDoneEmitting(): Boolean {
        if(emittingTime > 0L) {
            return timer.isMaxTime() && particles.size == 0
        } else {
            return particles.size == 0
        }
    }

    override fun addConfetti() {
        timer.updateEmitTime()
        particlesCreated++
        super.addConfetti()
    }

}