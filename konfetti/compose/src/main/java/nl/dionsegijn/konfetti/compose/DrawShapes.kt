package nl.dionsegijn.konfetti.compose

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Shape.Circle
import nl.dionsegijn.konfetti.core.models.Shape.DrawableShape
import nl.dionsegijn.konfetti.core.models.Shape.Rectangle
import nl.dionsegijn.konfetti.core.models.Shape.Square

/**
 * Draw a shape to `compose canvas`. Implementations are expected to draw within a square of size
 * `size` and must vertically/horizontally center their asset if it does not have an equal width
 * and height.
 */
fun Shape.draw(drawScope: DrawScope, particle: Particle, imageResource: ImageBitmap? = null) {
    when (this) {
        Circle -> {
            val offsetMiddle = particle.width / 2
            drawScope.drawCircle(
                color = Color(particle.color),
                center = Offset(particle.x + offsetMiddle, particle.y + offsetMiddle),
                radius = particle.width / 2
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
                size = Size(size, height)
            )
        }
        is DrawableShape -> {
            drawScope.drawIntoCanvas {
                if (tint) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable.colorFilter = BlendModeColorFilter(particle.color, BlendMode.SRC_IN)
                    } else {
                        drawable.setColorFilter(particle.color, PorterDuff.Mode.SRC_IN)
                    }
                } else {
                    drawable.alpha = (particle.alpha shl 24) or (particle.color and 0xffffff)
                }

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
