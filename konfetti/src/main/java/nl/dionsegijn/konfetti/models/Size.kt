package nl.dionsegijn.konfetti.models

/**
 * Created by dionsegijn on 3/26/17.
 * [size] the size of the confetti in size
 * [mass] each size has its own mass for slightly different behavior
 */
enum class Size(val size: Float, val mass: Float) {
    SMALL(40f, 5f),
    MEDIUM(60f, 10f),
    LARGE(80f, 15f)
}
