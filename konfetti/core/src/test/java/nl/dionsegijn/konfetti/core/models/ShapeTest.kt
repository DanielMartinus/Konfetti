package nl.dionsegijn.konfetti.core.models

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
}
