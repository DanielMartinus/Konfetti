package nl.dionsegijn.konfetti.json

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import nl.dionsegijn.konfetti.core.Drift
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig
import nl.dionsegijn.konfetti.core.models.CoreImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size

/**
 * Reads and writes parties as json, the format Konfetti Studio exports.
 *
 * ```
 * val parties = KonfettiJson.decode(json, density = LocalDensity.current.density)
 * KonfettiView(parties = parties)
 * ```
 */
object KonfettiJson {
    /** The newest file version this library reads */
    const val VERSION = 1

    private val json =
        Json {
            classDiscriminator = "type"
            ignoreUnknownKeys = true
            encodeDefaults = false
            explicitNulls = false
        }

    private val prettyJson = Json(json) { prettyPrint = true }

    /**
     * Reads the parties from a Konfetti json file.
     * @param density the screen density, to turn dp positions into pixels
     * @param images looks up the images that image shapes refer to by id
     * @throws KonfettiJsonException when the json isn't a valid Konfetti file
     */
    fun decode(
        json: String,
        density: Float = 1f,
        images: (id: String) -> CoreImage? = { null },
    ): List<Party> {
        val document =
            try {
                this.json.decodeFromString(KonfettiDocument.serializer(), json)
            } catch (e: SerializationException) {
                throw KonfettiJsonException("Not a Konfetti json file: ${e.message}", e)
            } catch (e: IllegalArgumentException) {
                throw KonfettiJsonException("Not a Konfetti json file: ${e.message}", e)
            }
        if (document.version > VERSION) {
            throw KonfettiJsonException("This file is version ${document.version}, update Konfetti to read it")
        }
        return document.parties.mapIndexed { i, party ->
            try {
                party.toParty(density, images)
            } catch (e: IllegalArgumentException) {
                throw KonfettiJsonException("Party ${i + 1}: ${e.message}", e)
            }
        }
    }

    /**
     * Writes [parties] as a Konfetti json file.
     * @param names optional names for the parties, in the same order
     * @param imageId the id to save an image shape under, image shapes without an id are left out
     */
    fun encode(
        parties: List<Party>,
        names: List<String?> = emptyList(),
        prettyPrint: Boolean = true,
        imageId: (CoreImage) -> String? = { null },
    ): String {
        val document = KonfettiDocument(parties = parties.mapIndexed { i, party -> party.toJson(names.getOrNull(i), imageId) })
        val format = if (prettyPrint) prettyJson else json
        return format.encodeToString(KonfettiDocument.serializer(), document)
    }
}

class KonfettiJsonException(message: String, cause: Throwable? = null) : IllegalArgumentException(message, cause)

private fun PartyJson.toParty(
    density: Float,
    images: (String) -> CoreImage?,
): Party {
    require(size.isNotEmpty()) { "size can't be empty" }
    require(colors.isNotEmpty()) { "colors can't be empty" }
    require(shapes.isNotEmpty()) { "shapes can't be empty" }
    return Party(
        angle = angle,
        spread = spread,
        speed = speed,
        maxSpeed = maxSpeed,
        damping = damping,
        size = size.map { Size(it.sizeInDp, it.mass, it.massVariance) },
        colors = colors.map { colorInt(it) },
        shapes = shapes.map { it.toShape(images) },
        timeToLive = timeToLive,
        fadeOutEnabled = fadeOutEnabled,
        position = position.toPosition(density),
        delay = delay,
        rotation =
            with(rotation) { Rotation(enabled, speed, variance, multiplier2D, multiplier3D, faceDirection) },
        emitter = emitter.toConfig(),
        drift = with(drift) { Drift(enabled, sway, frequency, variance, tilt, drag) },
    )
}

private fun EmitterJson.toConfig(): EmitterConfig {
    val emitter = Emitter(duration)
    if (perSecond != null) return emitter.perSecond(perSecond)
    requireNotNull(max) { "emitter needs max or perSecond" }
    return emitter.max(max)
}

private fun ShapeJson.toShape(images: (String) -> CoreImage?): Shape =
    when (this) {
        ShapeJson.Circle -> Shape.Circle
        ShapeJson.Square -> Shape.Square
        is ShapeJson.Rectangle -> Shape.Rectangle(heightRatio)
        is ShapeJson.Polygon -> Shape.Polygon(points.map { Shape.Polygon.Point(it.x, it.y) })
        is ShapeJson.Ribbon -> Shape.Ribbon(minLength, maxLength, loopSpacing, thickness, spin, bend, irregularity)
        is ShapeJson.Image -> {
            val image = requireNotNull(images(id)) { "no image registered for \"$id\"" }
            Shape.DrawableShape(image, tint, applyAlpha)
        }
    }

private fun PositionJson.toPosition(density: Float): Position =
    when (this) {
        is PositionJson.Relative -> Position.Relative(x, y)
        is PositionJson.Absolute -> {
            require(unit == UNIT_PX || unit == UNIT_DP) { "unknown unit \"$unit\", use px or dp" }
            val scale = if (unit == UNIT_DP) density else 1f
            Position.Absolute(x * scale, y * scale)
        }
        is PositionJson.Between -> {
            val from = min.toPosition(density)
            val to = max.toPosition(density)
            when {
                from is Position.Relative && to is Position.Relative -> from.between(to)
                from is Position.Absolute && to is Position.Absolute -> from.between(to)
                else -> throw IllegalArgumentException("between needs two relative or two absolute positions")
            }
        }
    }

private fun Party.toJson(
    name: String?,
    imageId: (CoreImage) -> String?,
) = PartyJson(
    name = name,
    angle = angle,
    spread = spread,
    speed = speed,
    maxSpeed = maxSpeed,
    damping = damping,
    size = size.map { SizeJson(it.sizeInDp, it.mass, it.massVariance) },
    colors = colors.map { colorHex(it) },
    shapes = shapes.mapNotNull { it.toJson(imageId) },
    timeToLive = timeToLive,
    fadeOutEnabled = fadeOutEnabled,
    position = position.toJson(),
    delay = delay,
    rotation =
        with(rotation) { RotationJson(enabled, speed, variance, multiplier2D, multiplier3D, faceDirection) },
    emitter =
        if (emitter.isPerSecond) {
            EmitterJson(emitter.emittingTime, perSecond = emitter.amount)
        } else {
            EmitterJson(emitter.emittingTime, max = emitter.amount)
        },
    drift = with(drift) { DriftJson(enabled, sway, frequency, variance, tilt, drag) },
)

private fun Shape.toJson(imageId: (CoreImage) -> String?): ShapeJson? =
    when (this) {
        Shape.Circle -> ShapeJson.Circle
        Shape.Square -> ShapeJson.Square
        is Shape.Rectangle -> ShapeJson.Rectangle(heightRatio)
        is Shape.Polygon -> ShapeJson.Polygon(points.map { PointJson(it.x, it.y) })
        is Shape.Ribbon -> ShapeJson.Ribbon(minLength, maxLength, loopSpacing, thickness, spin, bend, irregularity)
        is Shape.DrawableShape -> imageId(image)?.let { ShapeJson.Image(it, tint, applyAlpha) }
    }

private fun Position.toJson(): PositionJson =
    when (this) {
        is Position.Relative -> PositionJson.Relative(x, y)
        is Position.Absolute -> PositionJson.Absolute(x, y)
        is Position.Between -> PositionJson.Between(min.toJson(), max.toJson())
    }

private fun colorInt(hex: String): Int {
    val digits = hex.removePrefix("#")
    require(digits.length == 6) { "color \"$hex\" should look like #ff726d" }
    return requireNotNull(digits.toIntOrNull(16)) { "color \"$hex\" should look like #ff726d" }
}

private fun colorHex(color: Int): String = "#" + (color and 0xFFFFFF).toString(16).padStart(6, '0')
