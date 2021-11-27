package nl.dionsegijn.konfetti_core._new.NewEmitter

import java.util.concurrent.TimeUnit

// TODO rename to Emitter when done
data class EmitterBase(
    val duration: Long,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
) {
    fun max(amount: Int): EmitterConfig = EmitterConfig(this).max(amount)

    fun perSecond(amount: Int): EmitterConfig = EmitterConfig(this).perSecond(amount)
}

class EmitterConfig(
    emitterBase: EmitterBase
) {

    /** Max time allowed to emit in milliseconds */
    var emittingTime: Long = 0

    /** Amount of time needed for each particle creation in milliseconds */
    var amountPerMs: Float = 0f

    init {
        val (duration, timeUnit) = emitterBase
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

