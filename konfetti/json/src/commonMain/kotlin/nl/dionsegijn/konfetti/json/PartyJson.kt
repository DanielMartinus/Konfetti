package nl.dionsegijn.konfetti.json

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// The json shapes of a party. Field names and defaults match Party, so a file only lists what differs.

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class KonfettiDocument(
    @EncodeDefault val version: Int = KonfettiJson.VERSION,
    val parties: List<PartyJson>,
    // Studio keeps its screens and names here, apps can ignore it
    val meta: JsonObject? = null,
)

@Serializable
internal data class PartyJson(
    val name: String? = null,
    val angle: Int = 0,
    val spread: Int = 360,
    val speed: Float = 30f,
    val maxSpeed: Float = 0f,
    val damping: Float = 0.9f,
    val size: List<SizeJson> = listOf(SizeJson(6, mass = 4f), SizeJson(8), SizeJson(10, mass = 6f)),
    val colors: List<String> = listOf("#fce18a", "#ff726d", "#f4306d", "#b48def"),
    val shapes: List<ShapeJson> = listOf(ShapeJson.Square, ShapeJson.Circle),
    val timeToLive: Long = 2000,
    val fadeOutEnabled: Boolean = true,
    val position: PositionJson = PositionJson.Relative(0.5, 0.5),
    val delay: Int = 0,
    val rotation: RotationJson = RotationJson(),
    val emitter: EmitterJson,
    val drift: DriftJson = DriftJson(),
)

@Serializable
internal data class SizeJson(
    val sizeInDp: Int,
    val mass: Float = 5f,
    val massVariance: Float = 0.2f,
)

@Serializable
internal data class RotationJson(
    val enabled: Boolean = true,
    val speed: Float = 1f,
    val variance: Float = 0.5f,
    val multiplier2D: Float = 8f,
    val multiplier3D: Float = 1.5f,
    val faceDirection: Boolean = false,
)

@Serializable
internal data class DriftJson(
    val enabled: Boolean = false,
    val sway: Float = 20f,
    val frequency: Float = 1f,
    val variance: Float = 0.5f,
    val tilt: Float = 30f,
    val drag: Float = 0f,
)

/** Set either [max] or [perSecond]. A [duration] of 0 emits forever. */
@Serializable
internal data class EmitterJson(
    val duration: Long,
    val max: Int? = null,
    val perSecond: Int? = null,
)

@Serializable
internal sealed interface ShapeJson {
    @Serializable
    @SerialName("circle")
    data object Circle : ShapeJson

    @Serializable
    @SerialName("square")
    data object Square : ShapeJson

    @Serializable
    @SerialName("rectangle")
    data class Rectangle(val heightRatio: Float) : ShapeJson

    @Serializable
    @SerialName("polygon")
    data class Polygon(val points: List<PointJson>) : ShapeJson

    @Serializable
    @SerialName("ribbon")
    data class Ribbon(
        val minLength: Float = 60f,
        val maxLength: Float = 120f,
        val loopSpacing: Float = 10f,
        val thickness: Float = 0.6f,
        val spin: Float = 1f,
        val bend: Float = 0.5f,
        val irregularity: Float = 0.5f,
        val variance: Float = 0.5f,
        val shading: Float = 0.5f,
    ) : ShapeJson

    /** An image the app registers under [id], see [KonfettiJson.decode] */
    @Serializable
    @SerialName("image")
    data class Image(val id: String, val tint: Boolean = true, val applyAlpha: Boolean = true) : ShapeJson
}

@Serializable
internal data class PointJson(val x: Float, val y: Float)

@Serializable
internal sealed interface PositionJson {
    @Serializable
    @SerialName("relative")
    data class Relative(val x: Double, val y: Double) : PositionJson

    /** In pixels, or in dp when [unit] is "dp" so it looks the same on every screen */
    @Serializable
    @SerialName("absolute")
    data class Absolute(val x: Float, val y: Float, val unit: String = UNIT_PX) : PositionJson

    @Serializable
    @SerialName("between")
    data class Between(val min: PositionJson, val max: PositionJson) : PositionJson
}

internal const val UNIT_PX = "px"
internal const val UNIT_DP = "dp"
