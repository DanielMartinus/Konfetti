package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.image.ImageStore
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreImagesTest {
    @Test
    fun `a Compose image is not put in the drawable store`() {
        val party = Party(emitter = Emitter(100L).max(1), shapes = listOf(imageShape(ColorPainter(Color.Red))))
        assertEquals(party, storeImages(party, ImageStore()))
    }
}
