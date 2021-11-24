package nl.dionsegijn.konfetti_core.emitters

/**
 * Created by dionsegijn on 9/03/17.
 *
 * BurstEmitter creates all confetti at once when [RenderSystem] is started
 */
class BurstEmitter constructor(
    private val amountOfParticles: Int
) : Emitter() {

    private var isStarted = false

    /**
     * Create all confetti when the [RenderSystem] started, only do this once to render all
     * particles at the same time
     */
    override fun createConfetti(deltaTime: Float) {
        if (!isStarted) {
            isStarted = true
            for (i in 1..amountOfParticles) {
                if (i >= 1000) break
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
