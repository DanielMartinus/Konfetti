package nl.dionsegijn.konfetti.core.models

/**
 * @property sizeInDp the size of the confetti in dip
 * @property mass each size can have its own mass for slightly different behavior. For example, the closer
 * the mass is to zero the easier it will accelerate but the slower it will will fall down due to gravity.
 * @property massVariance create slight randomness how particles react to gravity. This variance
 * is a percentage based on [mass]. The higher the variance the bigger the difference in mass between
 * each particle is. Default is 0.2f for a slight difference in mass for each particle.
 */
data class Size(val sizeInDp: Int, val mass: Float = 5f, val massVariance: Float = 0.2f) {
    init {
        require(mass != 0F) { "mass=$mass must be != 0" }
    }

    companion object {
        val SMALL: Size = Size(sizeInDp = 6, mass = 4f)
        val MEDIUM: Size = Size(8)
        val LARGE: Size = Size(10, mass = 6f)
    }
}
