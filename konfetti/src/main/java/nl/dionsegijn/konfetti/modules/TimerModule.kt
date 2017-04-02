package nl.dionsegijn.konfetti.modules

/**
 * Created by dionsegijn on 3/31/17.
 */
class TimerModule {

    /**
     * Time the particle system started emitting
     */
    private var startTimestamp: Long = 0L
    /**
     * Timestamp of latest emitted particle
     */
    private var lastEmitTimestamp: Long = 0L

    fun start() {
        startTimestamp = System.currentTimeMillis()
    }

    fun reset() {
        startTimestamp = 0L
    }

    fun getElapsedTimeFromStart() : Long {
        return System.currentTimeMillis() - startTimestamp
    }

    fun getElapsedTimeLastEmit() : Long {
        return System.currentTimeMillis() - lastEmitTimestamp
    }

    fun updateEmitTime() {
        lastEmitTimestamp = System.currentTimeMillis()
    }

    fun isStarted() : Boolean {
        return startTimestamp != 0L
    }

    // TODO add max emitting time

}
