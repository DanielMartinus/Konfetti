package nl.dionsegijn.konfetti.models

import java.util.*

/**
 * Location module keeping tracking of the location values
 * The module handles both single points and multiple points in space
 * If a range is set (betweenX or betweenY) a random point will be returned
 */
class LocationModule(val random: Random) {

    private var minX: Float = 0f
    private var maxX: Float? = null

    private var minY: Float = 0f
    private var maxY: Float? = null

    val x: Float; get() {
        if (maxX == null) {
            return minX
        } else {
            return random.nextFloat().times(maxX!!.minus(minX)) + minX
        }
    }

    val y: Float; get() {
        if (maxY == null) {
            return minY
        } else {
            return random.nextFloat().times(maxY!!.minus(minY)) + minY
        }
    }

    fun betweenX(minX: Float, maxX: Float?) {
        this.minX = minX
        this.maxX = maxX
    }

    fun setX(x: Float) {
        this.minX = x
    }

    fun betweenY(minY: Float, maxY: Float?) {
        this.minY = minY
        this.maxY = maxY
    }

    fun setY(x: Float) {
        this.minY = x
    }

}
