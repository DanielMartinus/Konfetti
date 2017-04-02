package nl.dionsegijn.konfetti.modules

class TimerModule {

    /**
     * Timestamp the particle system started emitting
     * [startTimestamp] is set when [start] is called
     */
    private var startTimestamp: Long = 0L

    /**
     * Indicating how long emitting is allowed in milliseconds
     * endTimestamp checks against startTimestamp
     */
    private var endTimestamp: Long = 0L

    /**
     * Timestamp of last emitted particle
     */
    private var lastEmitTimestamp: Long = 0L

    fun start(maxEmitTime: Long = 0L) {
        this.endTimestamp = maxEmitTime
        startTimestamp = System.currentTimeMillis()
    }

    fun reset() {
        startTimestamp = 0L
        endTimestamp = 0L
        lastEmitTimestamp = 0L
    }

    /**
     * @return time in milliseconds between start and current timestamp
     */
    fun getElapsedTimeFromStart() : Long {
        return System.currentTimeMillis() - startTimestamp
    }

    /**
     * Last emitted timestamp should be updated by calling [updateEmitTime]
     * @return time in milliseconds between now and last emitted timestamp
     */
    fun getElapsedTimeLastEmit() : Long {
        return System.currentTimeMillis() - lastEmitTimestamp
    }

    /**
     * If the timer has reached the end
     * @return true if the elapsed time has passed endTimestamp or when endTimestamp is lower than 0
     */
    fun isMaxTime() : Boolean {
        return getElapsedTimeFromStart() < endTimestamp || endTimestamp <= 0
    }

    /**
     * Updating emit timestamp
     */
    fun updateEmitTime() {
        lastEmitTimestamp = System.currentTimeMillis()
    }

    /**
     * @return boolean if [start] is called
     * */
    fun isStarted() : Boolean {
        return startTimestamp != 0L
    }

}
