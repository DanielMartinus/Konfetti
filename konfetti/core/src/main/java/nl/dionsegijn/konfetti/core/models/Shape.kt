package nl.dionsegijn.konfetti.core.models

sealed interface Shape {
    object Circle : Shape {
        // Default replacement for RectF
        val rect = CoreRectImpl()
    }

    object Square : Shape

    class Rectangle(
        /** The ratio of height to width. Must be within range [0, 1] */
        val heightRatio: Float,
    ) : Shape {
        init {
            require(heightRatio in 0f..1f)
        }
    }

    /**
     * A drawable shape
     * @param image CoreImage
     * @param tint Set to `false` to opt out of tinting the drawable, keeping its original colors.
     * @param applyAlpha Set to false to not apply alpha to drawables
     */
    data class DrawableShape(
        val image: CoreImage,
        val tint: Boolean = true,
        val applyAlpha: Boolean = true,
    ) : Shape {
        val heightRatio =
            if (image.height == -1 && image.width == -1) {
                // If the image has no intrinsic size, fill the available space.
                1f
            } else if (image.height == -1 || image.width == -1) {
                // Currently cannot handle an image with only one intrinsic dimension.
                0f
            } else {
                image.height.toFloat() / image.width.toFloat()
            }
    }
}
