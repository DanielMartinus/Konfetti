package nl.dionsegijn.konfetti.web

import kotlinx.browser.document
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.models.Shape
import kotlin.math.PI

/**
 * Draws particles on a 2D canvas, the same way KonfettiView draws them in Compose:
 * rotated around their center and squeezed horizontally for the 3D flip.
 */
internal class Painter {
    // Tinted copies of images, one per image and color, so tinting isn't redone every frame
    private val tinted = mutableMapOf<String, dynamic>()

    fun draw(
        context: dynamic,
        particles: List<Particle>,
    ) {
        particles.forEach { particle ->
            context.save()
            context.translate(particle.x + particle.width / 2, particle.y + particle.height / 2)
            context.rotate(particle.rotation * PI / 180)
            context.scale(particle.scaleX, 1)
            drawParticle(context, particle)
            context.restore()
        }
    }

    /** Drops the tinted copies of an image, when it's registered again */
    fun forget(id: String) {
        tinted.keys.filter { it.startsWith("$id|") }.forEach { tinted.remove(it) }
    }

    // Draws around 0, 0, the particle's center
    private fun drawParticle(
        context: dynamic,
        particle: Particle,
    ) {
        val size = particle.width
        val half = size / 2
        context.globalAlpha = particle.alpha / 255.0
        context.fillStyle = cssColor(particle.color)
        when (val shape = particle.shape) {
            Shape.Circle -> {
                context.beginPath()
                context.arc(0, 0, half, 0, 2 * PI)
                context.fill()
            }
            Shape.Square -> context.fillRect(-half, -half, size, size)
            is Shape.Rectangle -> context.fillRect(-half, -half, size, size * shape.heightRatio)
            is Shape.Polygon -> {
                context.beginPath()
                shape.points.forEachIndexed { i, point ->
                    val x = point.x * size - half
                    val y = point.y * size - half
                    if (i == 0) context.moveTo(x, y) else context.lineTo(x, y)
                }
                context.closePath()
                context.fill()
            }
            is Shape.DrawableShape -> {
                val image = shape.image as? WebImage ?: return
                val height = size * shape.heightRatio
                if (!shape.tint && !shape.applyAlpha) context.globalAlpha = 1.0
                val source = if (shape.tint) tint(image, particle.color) else image.source
                context.drawImage(source, -half, -height / 2, size, height)
            }
        }
    }

    // An image filled with one color where it isn't transparent, like Compose's SrcIn tint
    private fun tint(
        image: WebImage,
        color: Int,
    ): dynamic =
        tinted.getOrPut("${image.id}|$color") {
            val canvas = document.createElement("canvas").asDynamic()
            canvas.width = image.width
            canvas.height = image.height
            val context = canvas.getContext("2d")
            context.drawImage(image.source, 0, 0, image.width, image.height)
            context.globalCompositeOperation = "source-in"
            context.fillStyle = cssColor(color)
            context.fillRect(0, 0, image.width, image.height)
            canvas
        }
}

// Alpha is drawn with globalAlpha, so only the color part is used
private fun cssColor(color: Int): String = "#" + (color and 0xFFFFFF).toString(16).padStart(6, '0')
