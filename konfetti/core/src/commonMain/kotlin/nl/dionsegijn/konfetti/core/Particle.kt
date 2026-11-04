package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.models.Shape

/**
 * Particle holding exact data to instruct where and how to draw a particle
 * @param x the absolute x position on the canvas
 * @param y the absolute y position on the canvas
 * @param width the current width of the confetti
 * @param height the current height of a confetti
 * @param color the color that will be used to paint the confetti
 * @param rotation the current rotation of the confetti in degrees, including its lean when it drifts
 * @param scaleX the current scale of the confetti used to create a 3D rotation
 * @param shape the Shape of the confetti such as a circle, square of custom shape
 * @param alpha the transparency of the confetti between 0 - 255
 * @param ribbon how a [Shape.Ribbon] streamer hangs and spins right now, null for other shapes.
 * For a streamer, [height] is its length. Draw it with [nl.dionsegijn.konfetti.core.models.ribbonSegments].
 */
data class Particle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int,
    val rotation: Float,
    val scaleX: Float,
    val shape: Shape,
    val alpha: Int,
    val ribbon: RibbonBody? = null,
) {
    // Keeps the constructor from 2.x working for Java and for code built against it
    constructor(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Int,
        rotation: Float,
        scaleX: Float,
        shape: Shape,
        alpha: Int,
    ) : this(x, y, width, height, color, rotation, scaleX, shape, alpha, null)
}

/**
 * The moving parts of a [Shape.Ribbon] streamer at one moment.
 * Only Konfetti reads them, draw a streamer with [nl.dionsegijn.konfetti.core.models.ribbonSegments].
 */
class RibbonBody internal constructor(
    // How far it has turned around its own length, in radians
    internal val spin: Float,
    // How long it is in pixels
    internal val length: Float,
    // The distance between two loops of its curl, in pixels
    internal val loopSpacing: Float,
    // The line its body runs along in pixels, from its head (always 0, 0) to its tail
    internal val pathX: FloatArray,
    internal val pathY: FloatArray,
    // A phase in radians that makes this streamer's curl a little uneven along its length
    internal val unevenness: Float,
    // How much thicker or thinner its strip is, and how much stronger or weaker its shading
    internal val thicknessScale: Float,
    internal val contrast: Float,
) {
    // Equal by content, so the same moment replayed gives equal particles
    override fun equals(other: Any?): Boolean =
        other is RibbonBody &&
            other.spin == spin &&
            other.length == length &&
            other.loopSpacing == loopSpacing &&
            other.pathX.contentEquals(pathX) &&
            other.pathY.contentEquals(pathY) &&
            other.unevenness == unevenness &&
            other.thicknessScale == thicknessScale &&
            other.contrast == contrast

    override fun hashCode(): Int =
        listOf(
            spin,
            length,
            loopSpacing,
            pathX.contentHashCode(),
            pathY.contentHashCode(),
            unevenness,
            thicknessScale,
            contrast,
        ).hashCode()
}
