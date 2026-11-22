package nl.dionsegijn.konfetti.xml

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Build
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.RibbonSegments
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Circle
import nl.dionsegijn.konfetti.core.models.Shape.DrawableShape
import nl.dionsegijn.konfetti.core.models.Shape.Polygon
import nl.dionsegijn.konfetti.core.models.Shape.Rectangle
import nl.dionsegijn.konfetti.core.models.Shape.Ribbon
import nl.dionsegijn.konfetti.core.models.Shape.Square
import nl.dionsegijn.konfetti.core.models.ribbonSegments
import nl.dionsegijn.konfetti.xml.image.ImageStore

/**
 * Draws a [Ribbon] streamer. Its pieces are already in canvas pixels, hanging and bending,
 * so the canvas mustn't be rotated or flipped for it. The shared [paint] goes back to filling afterwards.
 */
internal fun Particle.drawRibbon(
    canvas: Canvas,
    paint: Paint,
    ribbonSegments: RibbonSegments,
) {
    if (!ribbonSegments(ribbonSegments)) return
    paint.style = Paint.Style.STROKE
    paint.strokeCap = Paint.Cap.ROUND
    for (i in 0 until ribbonSegments.count) {
        paint.color = ribbonSegments.color[i]
        paint.strokeWidth = ribbonSegments.width[i]
        canvas.drawLine(ribbonSegments.startX[i], ribbonSegments.startY[i], ribbonSegments.endX[i], ribbonSegments.endY[i], paint)
    }
    paint.style = Paint.Style.FILL
}

/**
 * Draw a shape to `canvas`. Implementations are expected to draw within a square of size
 * `size` and must vertically/horizontally center their asset if it does not have an equal width
 * and height.
 */
fun Shape.draw(
    canvas: Canvas,
    paint: Paint,
    size: Float,
    imageStore: ImageStore,
) = draw(canvas, paint, size, imageStore, Path())

internal fun Shape.draw(
    canvas: Canvas,
    paint: Paint,
    size: Float,
    imageStore: ImageStore,
    polygonPath: Path,
) {
    when (this) {
        Square -> canvas.drawRect(0f, 0f, size, size, paint)
        Circle -> {
            val radius = size / 2f
            canvas.drawCircle(radius, radius, radius, paint)
        }
        is Rectangle -> {
            val height = size * heightRatio
            val top = (size - height) / 2f
            canvas.drawRect(0f, top, size, top + height, paint)
        }
        is Polygon -> {
            polygonPath.rewind()
            points.forEachIndexed { i, point ->
                if (i == 0) polygonPath.moveTo(point.x * size, point.y * size) else polygonPath.lineTo(point.x * size, point.y * size)
            }
            polygonPath.close()
            canvas.drawPath(polygonPath, paint)
        }
        // Streamers need the whole particle, KonfettiView draws them with drawRibbon
        is Ribbon -> Unit
        is DrawableShape -> {
            val referenceImage = image
            if (referenceImage is ReferenceImage) {
                // Making use of the ImageStore for performance reasons, see ImageStore for more info
                val drawable = imageStore.getImage(referenceImage.reference) ?: return

                if (tint) {
                    val tintColor = if (applyAlpha) paint.color else paint.color or (0xFF shl 24)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable.colorFilter = BlendModeColorFilter(tintColor, BlendMode.SRC_IN)
                    } else {
                        drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                    }
                } else {
                    // Clear a tint left on the shared drawable by another shape
                    drawable.colorFilter = null
                }
                // Set the alpha every time, another shape may have faded the shared drawable
                drawable.alpha = if (!tint && applyAlpha) paint.alpha else 255

                val height = (size * heightRatio).toInt()
                val top = ((size - height) / 2f).toInt()

                drawable.setBounds(0, top, size.toInt(), top + height)
                drawable.draw(canvas)
            }
        }
    }
}
