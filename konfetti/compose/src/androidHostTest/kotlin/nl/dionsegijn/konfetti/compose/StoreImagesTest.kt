package nl.dionsegijn.konfetti.compose

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.image.DrawableImage
import nl.dionsegijn.konfetti.xml.image.ImageStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreImagesTest {
    private class RecordingDrawable : Drawable() {
        var mutated = false

        override fun mutate(): Drawable {
            mutated = true
            return this
        }

        override fun draw(canvas: Canvas) = Unit

        override fun setAlpha(alpha: Int) = Unit

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit

        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }

    @Test
    fun `a Compose image is not put in the drawable store`() {
        val party = Party(emitter = Emitter(100L).max(1), shapes = listOf(imageShape(ColorPainter(Color.Red))))
        assertEquals(party, storeImages(party, ImageStore()))
    }

    @Test
    fun `storing a drawable mutates it so shared resource state stays untouched`() {
        val drawable = RecordingDrawable()
        val store = ImageStore()
        store.storeImage(drawable)
        assertTrue(drawable.mutated)
    }

    @Test
    fun `stopping an unstarted party does not store its drawable and finishing releases stored drawables`() {
        val state = KonfettiState()
        val drawable = RecordingDrawable()
        val party = Party(emitter = Emitter(100L).max(1), shapes = listOf(Shape.DrawableShape(DrawableImage(drawable, 24, 24))))

        state.stop(party)
        assertNull((state.imageStore as ImageStore).getImage(drawable.hashCode()))

        state.start(party)
        state.startPending(pixelDensity = 1f)
        assertEquals(drawable, (state.imageStore as ImageStore).getImage(drawable.hashCode()))

        state.stop(party)
        assertNull((state.imageStore as ImageStore).getImage(drawable.hashCode()))
    }
}
