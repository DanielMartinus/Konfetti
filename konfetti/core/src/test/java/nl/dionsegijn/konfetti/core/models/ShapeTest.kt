package nl.dionsegijn.konfetti.core.models

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

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
        assertThrows(IllegalArgumentException::class.java) {
            Shape.Polygon(Shape.Polygon.Point(0f, 0f), Shape.Polygon.Point(1f, 1f))
        }
    }

    @Test
    fun `a polygon point outside the square is not allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
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
    fun `a star has two corners per point, all inside the square`() {
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
        assertThrows(IllegalArgumentException::class.java) { Shape.Polygon.star(points = 2) }
        assertThrows(IllegalArgumentException::class.java) { Shape.Polygon.star(innerRadius = 1.5f) }
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
}
