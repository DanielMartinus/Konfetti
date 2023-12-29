package nl.dionsegijn.konfetti.xml.image

import android.graphics.drawable.Drawable
import nl.dionsegijn.konfetti.core.models.CoreImage

data class DrawableImage(
    val drawable: Drawable,
    override val width: Int,
    override val height: Int,
) : CoreImage
