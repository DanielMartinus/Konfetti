package nl.dionsegijn.konfetti.modules

import java.util.concurrent.TimeUnit

class TimerModule {

    /**
     * Current time should be used when calculating
     * the elapsed time, it will be updated once when
     * rendering the particles in the renderSystem
     * @property currentTime in nanoseconds
     */
    var currentTime: Long = 0L

    /**
     * Timestamp the particle system started emitting
     * [startTimestamp] is set when [start] is called
     */
    private var startTimestamp: Long = 0L

    /**
     * Indicating how long emitting is allowed in nanoseconds
     * endTimestamp checks against startTimestamp
     */
    private var endTimestamp: Long = 0L

    /**
     * Timestamp of last emitted particle
     */
    private var lastEmitTimestamp: Long = 0L

    /**
     * Update currentTime in nanoseconds
     */
    fun updateCurrentTime(): Long {
        currentTime = System.nanoTime()
        return currentTime
    }

    /**
     * Start time module with [maxEmitTime] in milliseconds
     * maxEmitTime will be converted to nanoseconds
     */
    fun start(maxEmitTime: Long = 0L) {
        this.endTimestamp = TimeUnit.MILLISECONDS.toNanos(maxEmitTime)
        startTimestamp = currentTime
    }

    fun reset() {
        startTimestamp = 0L
        endTimestamp = 0L
        lastEmitTimestamp = 0L
    }

    /**
     * @return time in nanoseconds between start and current timestamp
     */
    fun getElapsedTimeFromStart(): Long {
        return currentTime - startTimestamp
    }

    /**
     * Last emitted timestamp should be updated by calling [updateEmitTime]
     * @return time in nanoseconds between now and last emitted timestamp
     */
    fun getElapsedTimeLastEmit(): Long {
        return currentTime - lastEmitTimestamp
    }

    /**
     * If the timer has reached the end
     * @return true if the elapsed time has passed endTimestamp or when endTimestamp is lower than 0
     */
    fun isMaxTime(): Boolean {
        return getElapsedTimeFromStart() > endTimestamp && endTimestamp > 0
    }

    /**
     * Updating emit timestamp
     */
    fun updateEmitTime() {
        lastEmitTimestamp = currentTime
    }

    /**
     * @return boolean if [start] is called
     * */
    fun isStarted(): Boolean {
        return startTimestamp != 0L
    }

}
