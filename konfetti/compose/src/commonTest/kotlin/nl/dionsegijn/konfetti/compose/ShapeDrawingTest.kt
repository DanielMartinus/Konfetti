package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import nl.dionsegijn.konfetti.core.Particle
import kotlin.test.Test
import kotlin.test.assertEquals

class ShapeDrawingTest {
    private fun fadingParticle(shape: nl.dionsegijn.konfetti.core.models.Shape) =
        Particle(
            x = 0f,
            y = 0f,
            width = 20f,
            height = 20f,
            color = 0x40ff726d,
            rotation = 0f,
            scaleX = 1f,
            shape = shape,
            alpha = 0x40,
        )

    @Test
    fun `tint with applyAlpha enabled fades with the particle`() {
        val shape = imageShape(ColorPainter(Color.Red), tint = true, applyAlpha = true)
        assertEquals(0x40ff726d, shape.tintColor(fadingParticle(shape)))
    }

    @Test
    fun `tint with applyAlpha disabled stays opaque while the particle fades`() {
        val shape = imageShape(ColorPainter(Color.Red), tint = true, applyAlpha = false)
        assertEquals(0xffff726d.toInt(), shape.tintColor(fadingParticle(shape)))
    }
}
