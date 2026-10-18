package nl.dionsegijn.konfetti.compose

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Circle
import nl.dionsegijn.konfetti.core.models.Shape.DrawableShape
import nl.dionsegijn.konfetti.core.models.Shape.Polygon
import nl.dionsegijn.konfetti.core.models.Shape.Rectangle
import nl.dionsegijn.konfetti.core.models.Shape.Square
import nl.dionsegijn.konfetti.xml.image.ImageStore

// Reused for every polygon so drawing doesn't create a path per confetti per frame
private val polygonPath = Path()

/**
 * Draw a shape to `compose canvas`. Implementations are expected to draw within a square of size
 * `size` and must vertically/horizontally center their asset if it does not have an equal width
 * and height.
 */
fun Shape.draw(
    drawScope: DrawScope,
    particle: Particle,
    imageResource: ImageBitmap? = null,
    imageStore: ImageStore,
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
                topLeft = Offset(particle.x, particle.y),
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
        is DrawableShape -> {
            val referenceImage = image
            if (referenceImage is ReferenceImage) {
                val drawable = imageStore.getImage(referenceImage.reference) ?: return

                drawScope.drawIntoCanvas {
                    // Making use of the ImageStore for performance reasons, see ImageStore for more info
                    if (tint) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            drawable.colorFilter = BlendModeColorFilter(particle.color, BlendMode.SRC_IN)
                        } else {
                            drawable.setColorFilter(particle.color, PorterDuff.Mode.SRC_IN)
                        }
                    } else {
                        // Clear a tint left on the shared drawable by another shape
                        drawable.colorFilter = null
                    }
                    // Set the alpha every time, another shape may have faded the shared drawable
                    drawable.alpha = if (!tint && applyAlpha) particle.alpha else 255

                    val size = particle.width
                    val height = (size * heightRatio).toInt()
                    val top = ((size - height) / 2f).toInt()

                    val x = particle.y.toInt()
                    val y = particle.x.toInt()
                    drawable.setBounds(y, top + x, size.toInt() + y, top + height + x)
                    drawable.draw(it.nativeCanvas)
                }
            }
        }
    }
}
