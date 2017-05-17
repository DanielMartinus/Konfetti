package nl.dionsegijn.konfetti.models

/**
 * Created by dionsegijn on 3/25/17.
 */
data class Vector(var x: Float = 0f, var y: Float = 0f) {
    fun add(v: Vector) {
        x += v.x
        y += v.y
    }

    fun mult(n: Float) {
        x *= n
        y *= n
    }

    fun div(n: Float) {
        x /= n
        y /= n
    }

    fun pow(n: Double) {
       x = Math.pow(x.toDouble(), n).toFloat()
       y = Math.pow(y.toDouble(), n).toFloat()
    }

    fun mag(): Float {
        return Math.sqrt((x * x).toDouble() + (y * y)).toFloat()
    }

    fun normalize() {
        val m = mag()
        if (m != 0f) {
            div(m)
        }
    }

    fun limit(max: Float) {
        if (mag() > max * max) {
            normalize()
            mult(max)
        }
    }

}
