package nl.dionsegijn.konfetti.core.emitter

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Emitter class that holds the duration that the emitter will create confetti particles
 */
data class Emitter(
    val duration: Long,
    val timeUnit: DurationUnit = DurationUnit.MILLISECONDS,
) {
    constructor(duration: Duration) : this(duration.inWholeMilliseconds, DurationUnit.MILLISECONDS)

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
    var maxParticles: Int = -1
        internal set

    /** The amount passed to [max] or [perSecond], so the config can be written back as it was made */
    var amount: Int = 0
        private set

    /** True when made with [perSecond], false when made with [max] */
    val isPerSecond: Boolean get() = maxParticles < 0

    init {
        val (duration, timeUnit) = emitter
        this.emittingTime = duration.toDuration(timeUnit).inWholeMilliseconds
    }

    /**
     * Amount of particles created over the duration that is set
     */
    fun max(amount: Int): EmitterConfig {
        this.amount = amount
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
        this.amount = amount
        maxParticles = -1
        this.amountPerMs = 1f / amount
        return this
    }

    // Equal by value, so two parties with the same emitter are equal
    override fun equals(other: Any?): Boolean =
        other is EmitterConfig &&
            other.emittingTime == emittingTime &&
            other.amountPerMs == amountPerMs &&
            other.maxParticles == maxParticles

    override fun hashCode(): Int = (emittingTime.hashCode() * 31 + amountPerMs.hashCode()) * 31 + maxParticles
}
