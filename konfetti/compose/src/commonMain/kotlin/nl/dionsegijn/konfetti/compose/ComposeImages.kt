package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import nl.dionsegijn.konfetti.core.models.CoreImage
import nl.dionsegijn.konfetti.core.models.Shape

/**
 * An [ImageBitmap] as confetti. Compose draws it directly, so it works on every platform.
 */
data class ImageBitmapImage(
    val bitmap: ImageBitmap,
) : CoreImage {
    override val width: Int get() = bitmap.width
    override val height: Int get() = bitmap.height
}

/**
 * A [Painter] as confetti, for example a vector from Compose resources.
 * A painter without a size of its own fills the confetti's square.
 */
data class PainterImage(
    val painter: Painter,
) : CoreImage {
    // -1 tells DrawableShape the painter has no size of its own
    override val width: Int get() = painter.intrinsicSize.dimension { width }
    override val height: Int get() = painter.intrinsicSize.dimension { height }
}

private inline fun Size.dimension(axis: Size.() -> Float): Int {
    if (this == Size.Unspecified) return -1
    val value = axis()
    if (!value.isFinite()) return -1
    if (value <= 0f) return 0
    return value.toInt().coerceAtLeast(1)
}

/**
 * Confetti shape that draws [bitmap].
 * @param tint Set to `false` to keep the image's own colors instead of the party colors.
 * @param applyAlpha Set to `false` to keep the image fully visible while it fades out.
 */
fun imageShape(
    bitmap: ImageBitmap,
    tint: Boolean = true,
    applyAlpha: Boolean = true,
): Shape.DrawableShape = Shape.DrawableShape(ImageBitmapImage(bitmap), tint, applyAlpha)

/**
 * Confetti shape that draws [painter], for example `painterResource(Res.drawable.heart)`.
 * @param tint Set to `false` to keep the image's own colors instead of the party colors.
 * @param applyAlpha Set to `false` to keep the image fully visible while it fades out.
 */
fun imageShape(
    painter: Painter,
    tint: Boolean = true,
    applyAlpha: Boolean = true,
): Shape.DrawableShape = Shape.DrawableShape(PainterImage(painter), tint, applyAlpha)
