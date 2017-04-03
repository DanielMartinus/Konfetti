package nl.dionsegijn.konfetti.emitters

import nl.dionsegijn.konfetti.models.ConfettiConfig
import nl.dionsegijn.konfetti.models.LocationModule
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.modules.TimerModule
import nl.dionsegijn.konfetti.modules.VelocityModule

/**
 * Created by dionsegijn on 4/2/17.
 */
open class BurstEmitter(timer: TimerModule, location: LocationModule, velocity: VelocityModule, sizes: Array<Size>, shapes: Array<Shape>, colors: IntArray, config: ConfettiConfig) : Emitter(timer, location, velocity, sizes, shapes, colors, config) {

    private var amountOfParticles = 0

    fun burst(amountOfParticles: Int): BurstEmitter {
        this.amountOfParticles = amountOfParticles
        for (i in 1..amountOfParticles) {
            addConfetti()
        }
        return this
    }

    override fun createConfetti() {
        // Skip implementation since all confetti is already created
    }

    /**
     * Done emitting when all particles disappeared
     */
    override fun isDoneEmitting(): Boolean {
        return particles.size == 0
    }
}
