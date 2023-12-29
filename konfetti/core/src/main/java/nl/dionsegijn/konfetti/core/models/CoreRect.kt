package nl.dionsegijn.konfetti.core.models

interface CoreRect {
    var x: Float
    var y: Float
    var width: Float
    var height: Float

    fun set(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
    ) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun contains(
        px: Int,
        py: Int,
    ): Boolean {
        return px >= x && px <= x + width && py >= y && py <= y + height
    }
}

class CoreRectImpl(
    override var x: Float = 0f,
    override var y: Float = 0f,
    override var width: Float = 0f,
    override var height: Float = 0f,
) : CoreRect {
    override fun set(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
    ) {
        super.set(x, y, width, height)
    }

    override fun contains(
        px: Int,
        py: Int,
    ): Boolean {
        return super.contains(px, py)
    }
}
