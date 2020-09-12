package nl.dionsegijn.konfetti.models

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.drawable.Drawable

interface Shape {
    /**
     * Draw a shape to `canvas`. Implementations are expected to draw within a square of size
     * `size` and must vertically/horizontally center their asset if it does not have an equal width
     * and height.
     */
    fun draw(canvas: Canvas, paint: Paint, size: Float)

    companion object {
        // Maintain binary and backwards compatibility with previous enum API.
        @JvmField
        @Deprecated("Use Square class, instead.", replaceWith = ReplaceWith("Shape.Square"))
        val RECT = Square

        @JvmField
        @Deprecated("Use Circle class, instead.", replaceWith = ReplaceWith("Shape.Circle"))
        val CIRCLE = Circle
    }

    object Square : Shape {
        override fun draw(
            canvas: Canvas,
            paint: Paint,
            size: Float
        ) {
            canvas.drawRect(0f, 0f, size, size, paint)
        }
    }

    class Rectangle(
        /** The ratio of height to width. Must be within range [0, 1] */
        private val heightRatio: Float
    ) : Shape {
        init {
            require(heightRatio in 0f..1f)
        }

        override fun draw(canvas: Canvas, paint: Paint, size: Float) {
            val height = size * heightRatio
            val top = (size - height) / 2f
            canvas.drawRect(0f, top, size, top + height, paint)
        }
    }

    object Circle : Shape {
        private val rect = RectF()

        override fun draw(
            canvas: Canvas,
            paint: Paint,
            size: Float
        ) {
            rect.set(0f, 0f, size, size)
            canvas.drawOval(rect, paint)
        }
    }

    data class DrawableShape(
        val drawable: Drawable,
        /** Set to `false` to opt out of tinting the drawable, keeping its original colors. */
        private val tint: Boolean = true
    ) : Shape {
        private val heightRatio =
            if (drawable.intrinsicHeight == -1 && drawable.intrinsicWidth == -1) {
                // If the drawable has no intrinsic size, fill the available space.
                1f
            } else if (drawable.intrinsicHeight == -1 || drawable.intrinsicWidth == -1) {
                // Currently cannot handle a drawable with only one intrinsic dimension.
                0f
            } else {
                drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth
            }

        override fun draw(canvas: Canvas, paint: Paint, size: Float) {
            if (tint) {
                drawable.setColorFilter(paint.color, PorterDuff.Mode.SRC_IN)
            } else {
                drawable.alpha = paint.alpha
            }

            val height = (size * heightRatio).toInt()
            val top = ((size - height) / 2f).toInt()

            drawable.setBounds(0, top, size.toInt(), top + height)
            drawable.draw(canvas)
        }
    }
}
