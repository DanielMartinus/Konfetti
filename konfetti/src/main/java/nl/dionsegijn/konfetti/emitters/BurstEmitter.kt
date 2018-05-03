package nl.dionsegijn.konfetti.emitters

/**
 * Created by dionsegijn on 9/03/17.
 *
 * BurstEmitter creates all confetti at once when [RenderSystem] is started
 */
class BurstEmitter : Emitter() {

    private var amountOfParticles = 0
        set(value) {
            field = if (value > 1000) 1000 else value
        }

    private var isStarted = false

    /**
     * The amount of particles you want to create at once
     */
    fun build(amountOfParticles: Int): Emitter {
        this.amountOfParticles = amountOfParticles
        isStarted = false
        return this
    }

    /**
     * Create all confetti when the [RenderSystem] started, only do this once to render all
     * particles at the same time
     */
    override fun createConfetti(deltaTime: Float) {
        if (!isStarted) {
            isStarted = true
            for (i in 1..amountOfParticles) {
                addConfettiFunc?.invoke()
            }
        }
    }

    /**
     * When the particles are rendered in [createConfetti] the emitter is finished
     * This is determined by [isStarted], it will only happen once
     */
    override fun isFinished(): Boolean = isStarted
}
