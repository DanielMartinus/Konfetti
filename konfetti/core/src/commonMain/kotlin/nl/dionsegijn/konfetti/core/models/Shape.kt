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
     * A curly paper streamer, also known as a serpentine. It's a long spiral that spins around its
     * own length while it falls, with a darker back and a lighter front, and it stays upright
     * instead of tumbling like flat confetti. Turn on [nl.dionsegijn.konfetti.core.Drift] to let it
     * sway, then its body trails behind its head like a rope.
     *
     * The confetti's [Size] sets how wide the spiral is. Each streamer picks a length between
     * [minLength] and [maxLength].
     *
     * @param minLength the shortest a streamer can be, in dp
     * @param maxLength the longest a streamer can be, in dp
     * @param loopSpacing the distance between two loops of the spiral in dp, so a longer streamer has more loops
     * @param thickness how wide the paper strip is, as a fraction of [loopSpacing]. Within (0, 1]
     * @param spin how many turns a second it spins around its own length. Set to 0 to hold the spiral still
     * @param bend how far its body trails behind its head when it sways, between 0 (stiff) and 1
     * @param irregularity how uneven the gaps between its loops are, between 0 (evenly wound) and 1. Uneven loops
     * bunch up and stretch out along the body and curl tighter toward the ends, and streamers differ more from each other.
     */
    class Ribbon
        @JvmOverloads
        constructor(
            val minLength: Float = 60f,
            val maxLength: Float = 120f,
            val loopSpacing: Float = 10f,
            val thickness: Float = 0.6f,
            val spin: Float = 1f,
            val bend: Float = 0.5f,
            val irregularity: Float = 0.5f,
        ) : Shape {
            init {
                require(minLength > 0f) { "minLength must be above 0" }
                require(maxLength >= minLength) { "maxLength can't be shorter than minLength" }
                require(loopSpacing > 0f) { "loopSpacing must be above 0" }
                require(thickness > 0f && thickness <= 1f) { "thickness must be above 0 and at most 1" }
                require(spin >= 0f) { "spin can't be negative" }
                require(bend in 0f..1f) { "bend must be within 0 and 1" }
                require(irregularity in 0f..1f) { "irregularity must be within 0 and 1" }
            }

            // Equal by value, so a party built inline in a composable doesn't count as new every recomposition
            override fun equals(other: Any?): Boolean =
                other is Ribbon &&
                    other.minLength == minLength &&
                    other.maxLength == maxLength &&
                    other.loopSpacing == loopSpacing &&
                    other.thickness == thickness &&
                    other.spin == spin &&
                    other.bend == bend &&
                    other.irregularity == irregularity

            override fun hashCode(): Int = listOf(minLength, maxLength, loopSpacing, thickness, spin, bend, irregularity).hashCode()
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
