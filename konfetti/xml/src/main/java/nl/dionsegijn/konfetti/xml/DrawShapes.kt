package nl.dionsegijn.konfetti.xml

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Build
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Circle
import nl.dionsegijn.konfetti.core.models.Shape.Circle.rect
import nl.dionsegijn.konfetti.core.models.Shape.DrawableShape
import nl.dionsegijn.konfetti.core.models.Shape.Rectangle
import nl.dionsegijn.konfetti.core.models.Shape.Square
import nl.dionsegijn.konfetti.xml.image.ImageStore
import kotlin.math.sqrt

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
) {
    when (this) {
        Square -> canvas.drawRect(0f, 0f, size, size, paint)
        Circle -> {
            rect.set(0f, 0f, size, size)
            canvas.drawOval(RectF(rect.x, rect.y, rect.width, rect.height), paint)
        }
        is Rectangle -> {
            val height = size * heightRatio
            val top = (size - height) / 2f
            canvas.drawRect(0f, top, size, top + height, paint)
        }
        is DrawableShape -> {
            val referenceImage = image
            if (referenceImage is ReferenceImage) {
                // Making use of the ImageStore for performance reasons, see ImageStore for more info
                val drawable = imageStore.getImage(referenceImage.reference) ?: return

                if (tint) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable.colorFilter = BlendModeColorFilter(paint.color, BlendMode.SRC_IN)
                    } else {
                        drawable.setColorFilter(paint.color, PorterDuff.Mode.SRC_IN)
                    }
                } else if (applyAlpha) {
                    drawable.alpha = paint.alpha
                }

                val height = (size * heightRatio).toInt()
                val top = ((size - height) / 2f).toInt()

                drawable.setBounds(0, top, size.toInt(), top + height)
                drawable.draw(canvas)
            }
        }
        Shape.Triangle -> {
            val triangleHeight = size * sqrt(3.0) / 2
            val trianglePath =
                Path().apply {
                    moveTo(size / 2, 0f)
                    lineTo(size, triangleHeight.toFloat())
                    lineTo(0f, triangleHeight.toFloat())
                }
            canvas.drawPath(trianglePath, paint)
        }
    }
}
