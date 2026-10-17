package nl.dionsegijn.konfetti.core.emitter

import java.util.concurrent.TimeUnit

/**
 * Emitter class that holds the duration that the emitter will create confetti particles
 */
data class Emitter(
    val duration: Long,
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
) {
    /**
     * Max amount of particles that will be created over the duration that is set
     */
    fun max(amount: Int): EmitterConfig = EmitterConfig(this).max(amount)

    /**
     * Amount of particles that will be created per second
     */
    fun perSecond(amount: Int): EmitterConfig = EmitterConfig(this).perSecond(amount)
}

/**
 * EmitterConfig class that will gold the Emitter configuration and amount of particles that
 * will be created over certain time
 */
class EmitterConfig(
    emitter: Emitter,
) {
    /** Max time allowed to emit in milliseconds */
    var emittingTime: Long = 0

    /** Amount of time needed for each particle creation in milliseconds */
    var amountPerMs: Float = 0f

    /**
     * Upper bound on particles this emitter may create.
     * Negative means there is no bound, which is what [Emitter.perSecond] uses.
     */
    internal var maxParticles: Int = -1

    init {
        val (duration, timeUnit) = emitter
        this.emittingTime = TimeUnit.MILLISECONDS.convert(duration, timeUnit)
    }

    /**
     * Amount of particles created over the duration that is set
     */
    fun max(amount: Int): EmitterConfig {
        maxParticles = amount.coerceAtLeast(0)
        // Float division so a count larger than the duration in milliseconds
        // cannot collapse to a 0 interval and spawn an unbounded burst.
        amountPerMs =
            if (amount <= 0) {
                Float.POSITIVE_INFINITY
            } else {
                (emittingTime.toFloat() / amount) / 1000f
            }
        return this
    }

    /**
     * Amount of particles that will be created per second
     */
    fun perSecond(amount: Int): EmitterConfig {
        this.amountPerMs = 1f / amount
        return this
    }
}
