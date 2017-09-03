package nl.dionsegijn.konfetti.emitters

/**
 * Created by dionsegijn on 9/03/17.
 *
 * BurstEmitter creates a bunch of particles when [build] is called.
 * All the particles will be rendered at once by the [RenderSystem]
 * once it's active.
 */
class BurstEmitter: Emitter() {

    private var amountOfParticles = 0
        set(value) {
            field = if(value > 1000) 1000 else value
        }

    /**
     * The amount of particles you want to create at once
     */
     fun build(amountOfParticles: Int): Emitter {
        this.amountOfParticles = amountOfParticles
        for (i in 1..amountOfParticles) {
            addConfettiFunc?.invoke()
        }
        return this
    }

    /**
     * Skip implementation, all confetti is already created in the [build] function
     */
    override fun createConfetti(deltaTime: Float) {}

    /**
     * Tell the [RenderSystem] right away that the emitter is finished creating particles
     * since it's already done in [build]
     */
    override fun isFinished(): Boolean = true
}
