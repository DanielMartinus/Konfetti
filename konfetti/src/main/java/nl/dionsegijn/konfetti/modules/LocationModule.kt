package nl.dionsegijn.konfetti.modules

import java.util.Random

/**
 * Location module keeping track of the locations
 * The module handles both single points and multiple points
 * If a range is set (betweenX or betweenY) a random point will be picked
 */
class LocationModule(private val random: Random) {

    private var minX: Float = 0f
    private var maxX: Float? = null

    private var minY: Float = 0f
    private var maxY: Float? = null

    val x: Float; get() {
        return if (maxX == null) {
            minX
        } else {
            random.nextFloat().times(maxX!!.minus(minX)) + minX
        }
    }

    val y: Float; get() {
        return if (maxY == null) {
            minY
        } else {
            random.nextFloat().times(maxY!!.minus(minY)) + minY
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
