package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.models.CoreImageStore
import nl.dionsegijn.konfetti.core.models.RibbonSegments
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Circle
import nl.dionsegijn.konfetti.core.models.Shape.DrawableShape
import nl.dionsegijn.konfetti.core.models.Shape.Polygon
import nl.dionsegijn.konfetti.core.models.Shape.Rectangle
import nl.dionsegijn.konfetti.core.models.Shape.Ribbon
import nl.dionsegijn.konfetti.core.models.Shape.Square
import nl.dionsegijn.konfetti.core.models.ribbonSegments

// Reused for every polygon so drawing doesn't create a path per confetti per frame
private val polygonPath = Path()

// Reused for every streamer for the same reason
private val ribbonSegments = RibbonSegments()

/**
 * Draws a [Ribbon] streamer. Its pieces are already in canvas pixels, leaning and bending,
 * so this is drawn without the rotation and flip other confetti get.
 */
internal fun DrawScope.drawRibbon(particle: Particle) {
    // The colors already carry the particle's alpha
    if (!particle.ribbonSegments(ribbonSegments)) return
    for (i in 0 until ribbonSegments.count) {
        drawLine(
            color = Color(ribbonSegments.color[i]),
            start = Offset(ribbonSegments.startX[i], ribbonSegments.startY[i]),
            end = Offset(ribbonSegments.endX[i], ribbonSegments.endY[i]),
            strokeWidth = ribbonSegments.width[i],
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Draw a shape to `compose canvas`. Implementations are expected to draw within a square of size
 * `size` and must vertically/horizontally center their asset if it does not have an equal width
 * and height.
 */
internal fun Shape.drawShape(
    drawScope: DrawScope,
    particle: Particle,
    imageStore: CoreImageStore<*>?,
) {
    when (this) {
        Circle -> {
            val offsetMiddle = particle.width / 2
            drawScope.drawCircle(
                color = Color(particle.color),
                center = Offset(particle.x + offsetMiddle, particle.y + offsetMiddle),
                radius = particle.width / 2,
            )
        }
        Square -> {
            drawScope.drawRect(
                color = Color(particle.color),
                topLeft = Offset(particle.x, particle.y),
                size = Size(particle.width, particle.height),
            )
        }
        is Rectangle -> {
            val size = particle.width
            val height = size * heightRatio
            drawScope.drawRect(
                color = Color(particle.color),
                topLeft = Offset(particle.x, particle.y + (size - height) / 2),
                size = Size(size, height),
            )
        }
        is Polygon -> {
            val size = particle.width
            polygonPath.reset()
            points.forEachIndexed { i, point ->
                val x = particle.x + point.x * size
                val y = particle.y + point.y * size
                if (i == 0) polygonPath.moveTo(x, y) else polygonPath.lineTo(x, y)
            }
            polygonPath.close()
            drawScope.drawPath(path = polygonPath, color = Color(particle.color))
        }
        // Streamers are drawn by drawRibbon, without the transform
        is Ribbon -> Unit
        is DrawableShape ->
            when (val image = image) {
                is ImageBitmapImage -> drawScope.drawBitmap(this, image, particle)
                is PainterImage -> drawScope.drawPainter(this, image, particle)
                else -> drawScope.drawPlatformImage(this, particle, imageStore)
            }
    }
}

// Same rules as the Android drawables: tint with the confetti color, or fade the image itself
private fun DrawableShape.colorFilter(particle: Particle) = if (tint) ColorFilter.tint(Color(particle.color), BlendMode.SrcIn) else null

private fun DrawableShape.alpha(particle: Particle) = if (!tint && applyAlpha) particle.alpha / 255f else 1f

// The image keeps its aspect ratio and is centered vertically in the confetti's square
private fun DrawScope.drawBitmap(
    shape: DrawableShape,
    image: ImageBitmapImage,
    particle: Particle,
) {
    val size = particle.width
    val height = size * shape.heightRatio
    val top = (size - height) / 2f
    drawImage(
        image = image.bitmap,
        dstOffset = IntOffset(particle.x.toInt(), (particle.y + top).toInt()),
        dstSize = IntSize(size.toInt(), height.toInt()),
        alpha = shape.alpha(particle),
        colorFilter = shape.colorFilter(particle),
    )
}

private fun DrawScope.drawPainter(
    shape: DrawableShape,
    image: PainterImage,
    particle: Particle,
) {
    val size = particle.width
    val height = size * shape.heightRatio
    val top = (size - height) / 2f
    translate(left = particle.x, top = particle.y + top) {
        with(image.painter) {
            draw(Size(size, height), shape.alpha(particle), shape.colorFilter(particle))
        }
    }
}
