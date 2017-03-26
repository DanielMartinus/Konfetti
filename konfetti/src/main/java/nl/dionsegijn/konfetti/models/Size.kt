package nl.dionsegijn.konfetti.models

/**
 * Created by dionsegijn on 3/26/17.
 */
enum class Size(val pixels: Float, val mass: Float) {
    SMALL(40f, 5f),
    MEDIUM(60f, 10f),
    LARGE(80f, 15f)
}
