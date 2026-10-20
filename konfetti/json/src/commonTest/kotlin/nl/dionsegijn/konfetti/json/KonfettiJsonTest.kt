package nl.dionsegijn.konfetti.json

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KonfettiJsonTest {
    private val explode = Party(emitter = Emitter(100L).max(80))

    private fun roundTrip(party: Party): Party = KonfettiJson.decode(KonfettiJson.encode(listOf(party))).single()

    @Test
    fun `a default party round trips`() {
        assertEquals(explode, roundTrip(explode))
    }

    @Test
    fun `every field round trips`() {
        val party =
            Party(
                angle = 300,
                spread = 45,
                speed = 12f,
                maxSpeed = 50f,
                damping = 0.95f,
                size = listOf(Size(12, mass = 3f, massVariance = 0.5f)),
                colors = listOf(0x123456, 0xff0000),
                shapes = listOf(Shape.Rectangle(0.4f), Shape.Polygon.STAR, Shape.Circle),
                timeToLive = 3000,
                fadeOutEnabled = false,
                position = Position.Relative(0.1, 0.2).between(Position.Relative(0.9, 0.2)),
                delay = 400,
                rotation =
                    Rotation(
                        enabled = false,
                        speed = 2f,
                        variance = 0.1f,
                        multiplier2D = 4f,
                        multiplier3D = 3f,
                        faceDirection = true,
                    ),
                emitter = Emitter(0L).perSecond(30),
            )
        assertEquals(party, roundTrip(party))
    }

    @Test
    fun `max and perSecond keep how they were made`() {
        val parade = Party(emitter = Emitter(5000L).perSecond(30))
        val json = KonfettiJson.encode(listOf(explode, parade))
        assertTrue(json.contains("\"max\": 80"))
        assertTrue(json.contains("\"perSecond\": 30"))
    }

    @Test
    fun `only fields that differ from the defaults are written`() {
        val json = KonfettiJson.encode(listOf(explode.copy(angle = 90)), prettyPrint = false)
        assertEquals("""{"version":1,"parties":[{"angle":90,"emitter":{"duration":100,"max":80}}]}""", json)
    }

    @Test
    fun `a short file fills in the defaults`() {
        val party = KonfettiJson.decode("""{"version":1,"parties":[{"emitter":{"duration":100,"max":80}}]}""").single()
        assertEquals(explode, party)
    }

    @Test
    fun `absolute dp turns into pixels with the density`() {
        val json = """{"parties":[{"position":{"type":"absolute","x":10,"y":20,"unit":"dp"},"emitter":{"duration":0,"perSecond":5}}]}"""
        assertEquals(Position.Absolute(30f, 60f), KonfettiJson.decode(json, density = 3f).single().position)
    }

    @Test
    fun `image shapes are found by id`() {
        val heart = ReferenceImage(1, 24, 24)
        val party = explode.copy(shapes = listOf(Shape.DrawableShape(heart, tint = false)))
        val json = KonfettiJson.encode(listOf(party)) { "heart" }
        val decoded = KonfettiJson.decode(json) { id -> heart.takeIf { id == "heart" } }.single()
        assertEquals(party, decoded)
    }

    @Test
    fun `a missing image explains which one`() {
        val json = """{"parties":[{"shapes":[{"type":"image","id":"logo"}],"emitter":{"duration":100,"max":1}}]}"""
        val error = assertFailsWith<KonfettiJsonException> { KonfettiJson.decode(json) }
        assertTrue(error.message!!.contains("logo"))
    }

    @Test
    fun `a newer version is refused`() {
        assertFailsWith<KonfettiJsonException> { KonfettiJson.decode("""{"version":2,"parties":[]}""") }
    }

    @Test
    fun `an emitter needs an amount`() {
        assertFailsWith<KonfettiJsonException> { KonfettiJson.decode("""{"parties":[{"emitter":{"duration":100}}]}""") }
    }

    @Test
    fun `a Konfetti Studio export reads back`() {
        // Written by Studio's export with names and its screens included
        val json =
            """
            {
              "version": 1,
              "parties": [
                {
                  "name": "Burst",
                  "angle": 270,
                  "spread": 45,
                  "maxSpeed": 50,
                  "size": [{ "sizeInDp": 6, "mass": 4, "massVariance": 0.2 }, { "sizeInDp": 10, "mass": 6, "massVariance": 0.2 }],
                  "shapes": [{ "type": "polygon", "points": [{ "x": 0.5, "y": 0.067 }, { "x": 1, "y": 0.933 }, { "x": 0, "y": 0.933 }] }, { "type": "rectangle", "heightRatio": 0.4 }],
                  "timeToLive": 3000,
                  "position": { "type": "relative", "x": 0.5, "y": 1 },
                  "emitter": { "duration": 100, "max": 30 }
                },
                {
                  "name": "Rain",
                  "position": {
                    "type": "between",
                    "min": { "type": "absolute", "unit": "dp", "x": 0, "y": 0 },
                    "max": { "type": "absolute", "unit": "dp", "x": 393, "y": 0 }
                  },
                  "rotation": { "enabled": false, "speed": 1, "variance": 0.5, "multiplier2D": 8, "multiplier3D": 1.5 },
                  "emitter": { "duration": 0, "perSecond": 100 }
                }
              ],
              "meta": { "studio": { "name": "Checkout", "screens": [{ "id": "a", "widthDp": 393 }], "parties": [{ "hidden": false }] } }
            }
            """.trimIndent()
        val (burst, rain) = KonfettiJson.decode(json, density = 2f)
        assertEquals(Shape.Polygon.TRIANGLE, burst.shapes.first())
        assertEquals(listOf(Size.SMALL, Size.LARGE), burst.size)
        assertEquals(Emitter(100L).max(30), burst.emitter)
        assertEquals(Position.Absolute(0f, 0f).between(Position.Absolute(786f, 0f)), rain.position)
        assertEquals(Rotation.disabled(), rain.rotation)
        assertEquals(Emitter(0L).perSecond(100), rain.emitter)
    }

    @Test
    fun `unknown fields are ignored`() {
        val json = """{"parties":[{"emitter":{"duration":100,"max":80},"studio":{"locked":true}}],"meta":{"screens":[]}}"""
        assertFalse(KonfettiJson.decode(json).isEmpty())
    }
}
