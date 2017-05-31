package nl.dionsegijn.konfetti.models

/**
 * Created by dionsegijn on 3/26/17.
 * [size] the size of the confetti in pixels
 * [mass] each size has its own mass for slightly different behavior
 */
data class Size(val size: Float, val mass: Float = 5f)
