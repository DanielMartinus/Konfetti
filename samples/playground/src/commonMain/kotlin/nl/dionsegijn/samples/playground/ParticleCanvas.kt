package nl.dionsegijn.samples.playground

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import nl.dionsegijn.konfetti.compose.PainterImage
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.models.Shape

// Reused for every polygon so drawing doesn't create a path per confetti per frame
private val polygonPath = Path()

/**
 * Draws particles that [Replay] worked out for one moment on the timeline.
 * KonfettiView runs on the clock, so it can't show a moment you scrub to.
 * The drawing matches KonfettiView's, for the shapes the playground uses.
 */
@Composable
fun ParticleCanvas(
    particles: () -> List<Particle>,
    modifier: Modifier = Modifier,
) {
    // Reading the particles while drawing redraws each frame without recomposing the screen
    Canvas(modifier) {
        particles().forEach { particle ->
            withTransform({
                rotate(
                    degrees = particle.rotation,
                    pivot = Offset(particle.x + particle.width / 2, particle.y + particle.height / 2),
                )
                scale(scaleX = particle.scaleX, scaleY = 1f, pivot = Offset(particle.x + particle.width / 2, particle.y))
            }) {
                drawParticle(particle)
            }
        }
    }
}

private fun DrawScope.drawParticle(particle: Particle) {
    val color = Color(particle.color)
    when (val shape = particle.shape) {
        Shape.Circle -> {
            val radius = particle.width / 2
            drawCircle(color = color, center = Offset(particle.x + radius, particle.y + radius), radius = radius)
        }
        Shape.Square -> drawRect(color = color, topLeft = Offset(particle.x, particle.y), size = Size(particle.width, particle.height))
        is Shape.Rectangle ->
            drawRect(
                color = color,
                topLeft = Offset(particle.x, particle.y),
                size = Size(particle.width, particle.width * shape.heightRatio),
            )
        is Shape.Polygon -> {
            polygonPath.reset()
            shape.points.forEachIndexed { i, point ->
                val x = particle.x + point.x * particle.width
                val y = particle.y + point.y * particle.width
                if (i == 0) polygonPath.moveTo(x, y) else polygonPath.lineTo(x, y)
            }
            polygonPath.close()
            drawPath(path = polygonPath, color = color)
        }
        is Shape.DrawableShape -> {
            // The playground only uses painters, like the heart from Compose resources
            val image = shape.image as? PainterImage ?: return
            val height = particle.width * shape.heightRatio
            translate(left = particle.x, top = particle.y + (particle.width - height) / 2f) {
                with(image.painter) {
                    draw(
                        size = Size(particle.width, height),
                        alpha = if (!shape.tint && shape.applyAlpha) particle.alpha / 255f else 1f,
                        colorFilter = if (shape.tint) ColorFilter.tint(color, BlendMode.SrcIn) else null,
                    )
                }
            }
        }
    }
}
