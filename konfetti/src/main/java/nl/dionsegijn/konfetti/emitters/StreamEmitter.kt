package nl.dionsegijn.konfetti.emitters

import nl.dionsegijn.konfetti.models.LocationModule
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.modules.TimerModule
import nl.dionsegijn.konfetti.modules.VelocityModule

/**
 * Created by dionsegijn on 4/2/17.
 */
class StreamEmitter(location: LocationModule, velocity: VelocityModule, sizes: Array<Size>, shapes: Array<Shape>, colors: IntArray) : Emitter(location, velocity, sizes, shapes, colors) {

    /** [TimerModule] keeping track of time */
    private var timer = TimerModule()

    /** Max amount of particles allowed to be created */
    private var maxParticles = -1
    /** Keeping count of how many particles are created */
    private var particlesCreated = 0
    /** Max time in milliseconds allowed to emit */
    private var emittingTime: Int = 0
    /** Milliseconds per particle creation */
    var amountps: Double = 0.0

    fun emit(particlesPerSecond: Int, emittingTime: Int = 0, maxParticles: Int = -1): StreamEmitter {
        this.maxParticles = maxParticles
        this.emittingTime = emittingTime
        amountps = 1000.0 / particlesPerSecond
        return this
    }

    /**
     * If timer isn't started yet, set initial start time
     * Create the first confetti immediately and update the last emit time
     */
    override fun createConfetti() {
        if (!timer.isStarted()) {
            timer.start()
            this.addConfetti()
        }

        if(maxParticles > 0 && particlesCreated > maxParticles) {
            return
        }

        val elapsedTime = timer.getElapsedTimeLastEmit()

        // Check if particle should be created
        if (elapsedTime >= amountps && (timer.getElapsedTimeFromStart() < emittingTime || emittingTime <= 0)) {
            // could be that invalidate is slower than amount that should be created.
            // latency of 15 ms but need 1 particle per 3 ms = 5 particles
            // danger is that elapsed time will increase when more particles are on screen
            // will could grow forever and becomes too heavy.
            var newP = Math.floor(elapsedTime / amountps).toInt()
            if (newP > 3) newP = 3


//            Log.e("Konfetti", "Particles per second: " + newP + " amount alive: " + particles.size)
            for (i in 1..newP) {
                this.addConfetti()
            }
        }
    }

    override fun addConfetti() {
        timer.updateEmitTime()
        particlesCreated++
        super.addConfetti()
    }

}