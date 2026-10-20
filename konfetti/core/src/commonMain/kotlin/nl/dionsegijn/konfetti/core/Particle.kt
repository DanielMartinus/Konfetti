package nl.dionsegijn.konfetti.core

import nl.dionsegijn.konfetti.core.models.Shape

/**
 * Particle holding exact data to instruct where and how to draw a particle
 * @param x the absolute x position on the canvas
 * @param y the absolute y position on the canvas
 * @param width the current width of the confetti
 * @param height the current height of a confetti
 * @param color the color that will be used to paint the confetti
 * @param rotation the current rotation of the confetti in degrees
 * @param scaleX the current scale of the confetti used to create a 3D rotation
 * @param shape the Shape of the confetti such as a circle, square of custom shape
 * @param alpha the transparency of the confetti between 0 - 255
 * @param ribbon how a [Shape.Ribbon] is spinning and bending right now, null for other shapes.
 * For a ribbon, [height] is its length. Draw it with [nl.dionsegijn.konfetti.core.models.ribbonSegments].
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
    // Keeps the constructor from before ribbons were added, for code built against an older version
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
 * @param spin how far it has turned around its own length, in radians
 * @param length how long it is in pixels right now. A streamer shot out pays out to its full length as it flies
 * @param loopSpacing the distance between two loops of its spiral in pixels, wider while it's pulled through the air
 * @param pathX the line its body runs along, x part: points in pixels from its head (first, always 0) to its tail
 * (last), evenly spread along the body. It hangs up from the head, or follows the path the head took while it flies
 * @param pathY the same line, y part
 * @param unevenness a phase in radians that makes this streamer's spiral a little uneven along its length
 * @param thicknessScale how much thicker or thinner this streamer's strip is than its shape's thickness
 * @param contrast how much stronger or weaker this streamer's shading is than its shape's shading
 * @param coilScale how much narrower its coil is than the confetti's size, while it's pulled through the air
 */
class RibbonBody(
    val spin: Float,
    val length: Float,
    val loopSpacing: Float,
    val pathX: FloatArray,
    val pathY: FloatArray,
    val unevenness: Float = 0f,
    val thicknessScale: Float = 1f,
    val contrast: Float = 1f,
    val coilScale: Float = 1f,
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
            other.contrast == contrast &&
            other.coilScale == coilScale

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
            coilScale,
        ).hashCode()
}
