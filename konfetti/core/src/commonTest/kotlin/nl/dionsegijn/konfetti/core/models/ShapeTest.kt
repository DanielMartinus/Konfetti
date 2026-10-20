package nl.dionsegijn.konfetti.core.models

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import kotlin.math.floor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ShapeTest {
    @Test
    fun `rectangles with the same height ratio are equal`() {
        assertEquals(Shape.Rectangle(0.2f), Shape.Rectangle(0.2f))
        assertEquals(Shape.Rectangle(0.2f).hashCode(), Shape.Rectangle(0.2f).hashCode())
    }

    @Test
    fun `rectangles with a different height ratio are not equal`() {
        assertNotEquals(Shape.Rectangle(0.2f), Shape.Rectangle(0.5f))
    }

    @Test
    fun `a party built twice with a rectangle is equal`() {
        val emitter = Emitter(100L).max(10)
        val first = Party(shapes = listOf(Shape.Square, Shape.Rectangle(0.2f)), emitter = emitter)
        val second = Party(shapes = listOf(Shape.Square, Shape.Rectangle(0.2f)), emitter = emitter)
        assertEquals(first, second)
    }

    private fun Shape.Polygon.isInsideSquare() = points.all { it.x in 0f..1f && it.y in 0f..1f }

    @Test
    fun `polygons with the same points are equal`() {
        val first = Shape.Polygon(Shape.Polygon.Point(0.5f, 0f), Shape.Polygon.Point(1f, 1f), Shape.Polygon.Point(0f, 1f))
        val second = Shape.Polygon(listOf(Shape.Polygon.Point(0.5f, 0f), Shape.Polygon.Point(1f, 1f), Shape.Polygon.Point(0f, 1f)))
        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `polygons with different points are not equal`() {
        assertNotEquals(Shape.Polygon.TRIANGLE, Shape.Polygon.DIAMOND)
    }

    @Test
    fun `a polygon needs at least 3 points`() {
        assertFailsWith<IllegalArgumentException> {
            Shape.Polygon(Shape.Polygon.Point(0f, 0f), Shape.Polygon.Point(1f, 1f))
        }
    }

    @Test
    fun `a polygon point outside the square is not allowed`() {
        assertFailsWith<IllegalArgumentException> {
            Shape.Polygon(Shape.Polygon.Point(0f, 0f), Shape.Polygon.Point(1.5f, 0f), Shape.Polygon.Point(0f, 1f))
        }
    }

    @Test
    fun `changing the list a polygon was made from does not change the polygon`() {
        val points = mutableListOf(Shape.Polygon.Point(0.5f, 0f), Shape.Polygon.Point(1f, 1f), Shape.Polygon.Point(0f, 1f))
        val polygon = Shape.Polygon(points)
        points.add(Shape.Polygon.Point(0f, 0f))
        assertEquals(3, polygon.points.size)
    }

    @Test
    fun `a star has two corners per point and all are inside the square`() {
        val star = Shape.Polygon.star(points = 6)
        assertEquals(12, star.points.size)
        assertTrue(star.isInsideSquare())
    }

    @Test
    fun `a star starts with a point at the top`() {
        val top = Shape.Polygon.STAR.points.first()
        assertEquals(0.5f, top.x, 0.001f)
        assertEquals(0f, top.y, 0.001f)
    }

    @Test
    fun `a star needs at least 3 points and a dent within the square`() {
        assertFailsWith<IllegalArgumentException> { Shape.Polygon.star(points = 2) }
        assertFailsWith<IllegalArgumentException> { Shape.Polygon.star(innerRadius = 1.5f) }
    }

    @Test
    fun `the built-in polygons are inside the square`() {
        listOf(Shape.Polygon.TRIANGLE, Shape.Polygon.STAR, Shape.Polygon.DIAMOND).forEach {
            assertTrue(it.isInsideSquare())
        }
    }

    @Test
    fun `a party built twice with a polygon is equal`() {
        val emitter = Emitter(100L).max(10)
        val first = Party(shapes = listOf(Shape.Polygon.star(points = 6)), emitter = emitter)
        val second = Party(shapes = listOf(Shape.Polygon.star(points = 6)), emitter = emitter)
        assertEquals(first, second)
    }

    @Test
    fun `ribbons with the same settings are equal`() {
        assertEquals(Shape.Ribbon(40f, 80f), Shape.Ribbon(40f, 80f))
        assertEquals(Shape.Ribbon(40f, 80f).hashCode(), Shape.Ribbon(40f, 80f).hashCode())
        assertNotEquals(Shape.Ribbon(40f, 80f), Shape.Ribbon(40f, 90f))
    }

    @Test
    fun `ribbon settings stay in range`() {
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(minLength = 0f) }
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(minLength = 100f, maxLength = 50f) }
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(loopSpacing = 0f) }
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(thickness = 1.5f) }
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(spin = -1f) }
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(bend = 2f) }
        assertFailsWith<IllegalArgumentException> { Shape.Ribbon(irregularity = -0.1f) }
    }

    @Test
    fun `shade lightens and darkens but keeps alpha`() {
        val gray = 0x80808080.toInt()
        assertEquals(0x80, shade(gray, 0f) and 0xff)
        assertTrue((shade(gray, 0.5f) and 0xff) > 0x80)
        assertTrue((shade(gray, -0.5f) and 0xff) < 0x80)
        assertEquals(0x80, shade(gray, 0.5f) ushr 24)
    }

    // The gaps between the loops along a body 1 long with 20 loops, measured where the spiral passes the front
    private fun loopGaps(
        irregularity: Float,
        unevenness: Float = 1.1f,
    ): List<Float> {
        val steps = 4000
        var turned = 0f
        val total = (1..steps).sumOf { loopDensity((it - 0.5f) / steps, irregularity, unevenness).toDouble() }.toFloat() / steps
        val crossings = mutableListOf<Float>()
        for (i in 1..steps) {
            val before = turned
            turned += loopDensity((i - 0.5f) / steps, irregularity, unevenness) / steps / total * 20
            if (floor(turned) > floor(before)) crossings.add(i.toFloat() / steps)
        }
        return crossings.zipWithNext { a, b -> b - a }
    }

    @Test
    fun `an even ribbon has even gaps between its loops`() {
        val gaps = loopGaps(irregularity = 0f)
        assertTrue(gaps.max() - gaps.min() < 0.001f, "gaps $gaps")
    }

    @Test
    fun `an irregular ribbon bunches and stretches its loops`() {
        val gaps = loopGaps(irregularity = 1f)
        assertTrue(gaps.max() > gaps.min() * 2.5f, "gaps $gaps")
    }

    @Test
    fun `an irregular ribbon curls tighter at its ends`() {
        val gaps = loopGaps(irregularity = 1f)
        val middle = gaps.subList(3, gaps.size - 3).average()
        assertTrue(gaps.first() < middle && gaps.last() < middle, "gaps $gaps")
    }

    @Test
    fun `irregular ribbons differ from each other`() {
        assertNotEquals(loopGaps(irregularity = 1f, unevenness = 0.3f), loopGaps(irregularity = 1f, unevenness = 4f))
    }
}
