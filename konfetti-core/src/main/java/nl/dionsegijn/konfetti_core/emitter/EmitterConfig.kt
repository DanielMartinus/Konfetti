package nl.dionsegijn.konfetti_core.NewEmitter

import java.util.concurrent.TimeUnit

data class Emitter(
    val duration: Long,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
) {
    fun max(amount: Int): EmitterConfig = EmitterConfig(this).max(amount)

    fun perSecond(amount: Int): EmitterConfig = EmitterConfig(this).perSecond(amount)

    companion object {
        fun burst(amount: Int) = Emitter(duration = 100L).max(amount)

        fun streamParticlesPerSecond(particlesPerSecond: Int, durationInMillis: Long) =
            Emitter(duration = durationInMillis).perSecond(particlesPerSecond)

        fun streamMaxParticles(maxParticles: Int, durationInMillis: Long) =
            Emitter(durationInMillis).max(maxParticles)
    }
}

class EmitterConfig(
    emitter: Emitter
) {

    /** Max time allowed to emit in milliseconds */
    var emittingTime: Long = 0

    /** Amount of time needed for each particle creation in milliseconds */
    var amountPerMs: Float = 0f

    init {
        val (duration, timeUnit) = emitter
        if (duration == INDEFINITE) this.emittingTime = duration
        else this.emittingTime = TimeUnit.MILLISECONDS.convert(duration, timeUnit)
    }

    /**
     * Amount of particles created over the duration that is set
     */
    fun max(amount: Int): EmitterConfig {
        this.amountPerMs = (emittingTime / amount) / 1000f
        return this
    }

    /**
     * Amount of particles that will be created per second
     */
    fun perSecond(amount: Int): EmitterConfig {
        this.amountPerMs = 1f / amount
        return this
    }

    companion object {
        /**
         * Start an endless stream of particles by using this property in combination with
         * emittingTime. The stream of particles can only be stopped manually.
         */
        const val INDEFINITE = -2L
    }
}

