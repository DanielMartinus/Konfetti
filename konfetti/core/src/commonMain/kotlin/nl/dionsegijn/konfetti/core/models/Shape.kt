package nl.dionsegijn.konfetti.core.models

import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

        // Equal by value, so a party built inline in a composable doesn't count as new every recomposition
        override fun equals(other: Any?): Boolean = other is Rectangle && other.heightRatio == heightRatio

        override fun hashCode(): Int = heightRatio.hashCode()
    }

    /**
     * A shape made of straight lines between [points], filled with the confetti color.
     * It rotates, flips and fades like the other shapes.
     *
     * Use one of the built-ins like [TRIANGLE], [STAR] and [DIAMOND], make a star with [star],
     * or pass your own points.
     *
     * @param points at least 3 points, each a fraction of the confetti's square:
     * (0, 0) is the top-left corner and (1, 1) the bottom-right one.
     */
    class Polygon(points: List<Point>) : Shape {
        val points: List<Point> = points.toList()

        constructor(vararg points: Point) : this(points.toList())

        /**
         * A corner of a [Polygon], as a fraction of the confetti's square. Both values are within [0, 1].
         */
        data class Point(val x: Float, val y: Float)

        init {
            require(this.points.size >= 3) { "A polygon needs at least 3 points" }
            require(this.points.all { it.x in 0f..1f && it.y in 0f..1f }) { "Polygon points must be within 0 and 1" }
        }

        // Drawing looks a polygon up every frame, so the hash is worked out once
        private val hash = this.points.hashCode()

        // Equal by value, so a party built inline in a composable doesn't count as new every recomposition
        override fun equals(other: Any?): Boolean = other is Polygon && other.points == points

        override fun hashCode(): Int = hash

        companion object {
            /** A triangle with equal sides, pointing up */
            @JvmField
            val TRIANGLE =
                Polygon(
                    Point(0.5f, 0.067f),
                    Point(1f, 0.933f),
                    Point(0f, 0.933f),
                )

            /** A star with 5 points */
            @JvmField
            val STAR = star()

            /** A diamond like on a playing card, taller than it is wide */
            @JvmField
            val DIAMOND =
                Polygon(
                    Point(0.5f, 0f),
                    Point(0.85f, 0.5f),
                    Point(0.5f, 1f),
                    Point(0.15f, 0.5f),
                )

            /**
             * A star with [points] points, pointing up.
             * @param innerRadius how deep the dents between the points go, as a fraction of the outer size.
             * Lower values give thinner points.
             */
            @JvmStatic
            @JvmOverloads
            fun star(
                points: Int = 5,
                innerRadius: Float = 0.4f,
            ): Polygon {
                require(points >= 3) { "A star needs at least 3 points" }
                require(innerRadius in 0f..1f) { "innerRadius must be within 0 and 1" }
                val corners =
                    (0 until points * 2).map { i ->
                        // Every other corner is a dent. Start at the top, -90 degrees.
                        val radius = if (i % 2 == 0) 0.5f else 0.5f * innerRadius
                        val angle = -PI / 2 + i * PI / points
                        Point(
                            x = (0.5f + radius * cos(angle).toFloat()).coerceIn(0f, 1f),
                            y = (0.5f + radius * sin(angle).toFloat()).coerceIn(0f, 1f),
                        )
                    }
                return Polygon(corners)
            }
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
