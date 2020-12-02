package nl.dionsegijn.konfetti.models

/**
 * Created by dionsegijn on 3/25/17.
 */
data class Vector(var x: Float = 0f, var y: Float = 0f) {
    fun add(v: Vector) {
        x += v.x
        y += v.y
    }

    fun addScaled(v: Vector, s: Float) {
        x += v.x * s
        y += v.y * s
    }

    fun mult(n: Float) {
        x *= n
        y *= n
    }

    fun div(n: Float) {
        x /= n
        y /= n
    }
}
