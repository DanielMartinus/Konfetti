package nl.dionsegijn.konfetti.core.models

import android.graphics.RectF
import android.graphics.drawable.Drawable

sealed interface Shape {
    object Circle : Shape {
        val rect = RectF()
    }
    object Square : Shape
    class Rectangle(
        /** The ratio of height to width. Must be within range [0, 1] */
        val heightRatio: Float
    ) : Shape {
        init {
            require(heightRatio in 0f..1f)
        }
    }
    data class DrawableShape(
        val drawable: Drawable,
        /** Set to `false` to opt out of tinting the drawable, keeping its original colors. */
        val tint: Boolean = true
    ) : Shape {
        val heightRatio =
            if (drawable.intrinsicHeight == -1 && drawable.intrinsicWidth == -1) {
                // If the drawable has no intrinsic size, fill the available space.
                1f
            } else if (drawable.intrinsicHeight == -1 || drawable.intrinsicWidth == -1) {
                // Currently cannot handle a drawable with only one intrinsic dimension.
                0f
            } else {
                drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth
            }
    }
}
