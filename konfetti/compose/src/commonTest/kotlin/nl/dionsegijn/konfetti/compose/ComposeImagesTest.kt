package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeImagesTest {
    // A painter with a size of its own, like a vector resource
    private class SizedPainter(
        override val intrinsicSize: Size,
    ) : Painter() {
        override fun DrawScope.onDraw() = Unit
    }

    @Test
    fun `a painter without a size fills the square`() {
        assertEquals(1f, imageShape(ColorPainter(Color.Red)).heightRatio)
    }

    @Test
    fun `a painter keeps its own aspect ratio`() {
        assertEquals(0.5f, imageShape(SizedPainter(Size(40f, 20f))).heightRatio)
    }

    @Test
    fun `an image shape tints and fades by default`() {
        val shape = imageShape(ColorPainter(Color.Red))
        assertEquals(true, shape.tint)
        assertEquals(true, shape.applyAlpha)
    }

    @Test
    fun `painters with zero, partial or subpixel intrinsic sizes never produce NaN`() {
        assertEquals(0f, imageShape(SizedPainter(Size.Zero)).heightRatio)
        assertEquals(0f, imageShape(SizedPainter(Size(Float.NaN, 24f))).heightRatio)
        assertEquals(0f, imageShape(SizedPainter(Size(Float.POSITIVE_INFINITY, 24f))).heightRatio)
        assertEquals(1f, imageShape(SizedPainter(Size(0.4f, 0.4f))).heightRatio)
    }
}
