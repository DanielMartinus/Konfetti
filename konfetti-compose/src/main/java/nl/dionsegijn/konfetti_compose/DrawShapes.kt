package nl.dionsegijn.konfetti_compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Shape.*

/**
 * Draw a shape to `compose canvas`. Implementations are expected to draw within a square of size
 * `size` and must vertically/horizontally center their asset if it does not have an equal width
 * and height.
 */
fun Shape.draw(drawScope: DrawScope, particle: Particle) {
    when (this) {
        Circle -> {
            drawScope.drawCircle(
                color = Color(particle.color),
                center = Offset(particle.x, particle.y),
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
            val top = (size - height) / 2f
            drawScope.drawRect(
                color = Color(particle.color),
                topLeft = Offset(particle.x, particle.y),
                size = Size(size, height)
            )
        }
        is DrawableShape -> TODO()
    }
}
