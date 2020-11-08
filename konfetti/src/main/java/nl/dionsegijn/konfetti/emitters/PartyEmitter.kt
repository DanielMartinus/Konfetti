package nl.dionsegijn.konfetti.emitters

import nl.dionsegijn.konfetti.core.Emitter2
import nl.dionsegijn.konfetti.core.PartySystem

/**
 * Created by dionsegijn on 9/03/17.
 *
 * BurstEmitter creates all confetti at once when [RenderSystem] is started
 */
class PartyEmitter(val partySystem: PartySystem): Emitter2 {

    private var amountOfParticles = 0
        set(value) {
            field = if (value > 1000) 1000 else value
        }

    private var isStarted = false

    /**
     * The amount of particles you want to create at once
     */
    fun build(amountOfParticles: Int): PartyEmitter {
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
                partySystem.createConfetti()
            }
        }
    }

    /**
     * When the particles are rendered in [createConfetti] the emitter is finished
     * This is determined by [isStarted], it will only happen once
     */
    override fun isFinished(): Boolean = isStarted
}
