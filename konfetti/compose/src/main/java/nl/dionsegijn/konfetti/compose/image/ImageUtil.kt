package nl.dionsegijn.konfetti.xml.image

import android.graphics.drawable.Drawable
import nl.dionsegijn.konfetti.core.models.Shape

object ImageUtil {
    @JvmStatic
    fun loadDrawable(
        drawable: Drawable,
        tint: Boolean = true,
        applyAlpha: Boolean = true,
    ): Shape.DrawableShape {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val drawableImage = DrawableImage(drawable, width, height)
        return Shape.DrawableShape(drawableImage, tint, applyAlpha)
    }
}
