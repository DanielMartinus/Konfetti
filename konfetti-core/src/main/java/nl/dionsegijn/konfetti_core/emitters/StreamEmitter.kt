package nl.dionsegijn.konfetti_core.emitters

import java.util.concurrent.TimeUnit

/**
 * Stream emitter is a little more complex. It has several configurations for example:
 * - Creating x amount of particles in a certain time frame
 */
class StreamEmitter(duration: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS) : Emitter() {

    /** Max time allowed to emit in milliseconds */
    private var duration: Long = 0

    /* Keeping count of how many particles are created whilst running the emitter */
    private var particlesCreated = 0

    /** Elapsed time in milliseconds */
    private var elapsedTime: Float = 0f

    /** Amount of time needed for each particle creation in milliseconds */
    private var amountPerMs: Float = 0f

    /** Amount of time elapsed since last particle creation in milliseconds */
    private var createParticleMs: Float = 0f

    companion object {
        /**
         * Start an endless stream of particles by using this property in combination with
         * emittingTime. The stream of particles can only be stopped manually.
         */
        const val INDEFINITE = -2L
    }

    init {
        if (duration == INDEFINITE) this.duration = duration
        else this.duration = TimeUnit.MILLISECONDS.convert(duration, timeUnit)
    }

    /**
     * Amount of particles created over the duration that is set
     */
    fun max(amount: Int): StreamEmitter {
        this.amountPerMs = (duration / amount) / 1000f
        return this
    }

    /**
     * Amount of particles that will be created per second
     */
    fun perSecond(amount: Int): StreamEmitter {
        this.amountPerMs = 1f / amount
        return this
    }

    /**
     * If timer isn't started yet, set initial start time
     * Create the first confetti immediately and update the last emitting time
     */
    override fun createConfetti(deltaTime: Float) {
        createParticleMs += deltaTime

        // Check if particle should be created
        if (createParticleMs >= amountPerMs && !isTimeElapsed()) {
            // Calculate how many particle  to create in the elapsed time
            val amount: Int = (createParticleMs / amountPerMs).toInt()
            (1..amount).forEach { createParticle() }
            // Reset timer and add left over time for the next cycle
            createParticleMs %= amountPerMs
        }

        elapsedTime += deltaTime * 1000
    }

    private fun createParticle() {
        particlesCreated++
        addConfettiFunc?.invoke()
    }

    /**
     * If the [duration] is 0 it's not set and not relevant
     * If the emitting time is set check if [elapsedTime] exceeded the emittingTime
     */
    private fun isTimeElapsed(): Boolean {
        return when (duration) {
            0L -> false
            INDEFINITE -> false
            else -> elapsedTime >= duration
        }
    }

    /**
     * If the [duration] is set tell the [RenderSystem] the emitter is finished creating
     * particles when the elapsed time exceeded the emitting time.
     * If the [duration] is not set tell the [RenderSystem] that the emitter is finished
     * creating particles when [particlesCreated] exceeded [maxParticles]
     */
    override fun isFinished(): Boolean {
        return when {
            duration > 0L -> {
                elapsedTime >= duration
            }
            duration == INDEFINITE -> false
            else -> false
        }
    }
}
