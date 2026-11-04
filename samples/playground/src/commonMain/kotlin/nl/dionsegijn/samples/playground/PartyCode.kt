package nl.dionsegijn.samples.playground

import nl.dionsegijn.konfetti.compose.PainterImage
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

/**
 * Kotlin for [parties], written like the presets in the samples so it can be pasted into an app.
 * Only fields that differ from the defaults are printed.
 */
fun partyCode(parties: List<Party>): String {
    val blocks = parties.map { partyCode(it) }
    if (blocks.size == 1) return blocks.single()
    return "listOf(\n" + blocks.joinToString(",\n") { it.prependIndent("    ") } + ",\n)"
}

private fun partyCode(party: Party): String {
    val defaults = Party(emitter = party.emitter)
    val fields = mutableListOf<String>()
    with(party) {
        if (angle != defaults.angle) fields += "angle = ${angleName(angle)}"
        if (spread != defaults.spread) fields += "spread = $spread"
        if (speed != defaults.speed) fields += "speed = ${floatCode(speed)}"
        if (maxSpeed != defaults.maxSpeed) fields += "maxSpeed = ${floatCode(maxSpeed)}"
        if (damping != defaults.damping) fields += "damping = ${floatCode(damping)}"
        if (size != defaults.size) fields += "size = listOf(${size.joinToString { sizeCode(it) }})"
        if (colors != defaults.colors) fields += "colors = listOf(${colors.joinToString { colorCode(it) }})"
        if (shapes != defaults.shapes) fields += "shapes = listOf(${shapes.joinToString { shapeCode(it) }})"
        if (timeToLive != defaults.timeToLive) fields += "timeToLive = ${timeToLive}L"
        if (!fadeOutEnabled) fields += "fadeOutEnabled = false"
        if (position != defaults.position) fields += "position = ${positionCode(position)}"
        if (delay != defaults.delay) fields += "delay = $delay"
        if (rotation != Rotation()) fields += "rotation = ${rotationCode(rotation)}"
        fields += "emitter = ${emitterCode(party)}"
    }
    return "Party(\n" + fields.joinToString("") { "    $it,\n" } + ")"
}

/** How many confetti a party creates in total */
fun Party.particleCount(): Int = (emitter.emittingTime / 1000f / emitter.amountPerMs).roundToInt()

/** The same party, creating [count] confetti over the same time */
fun Party.withParticleCount(count: Int): Party {
    if (emitter.emittingTime <= 0L) return this
    return copy(emitter = Emitter(emitter.emittingTime.milliseconds).max(count))
}

private fun emitterCode(party: Party): String {
    val ms = party.emitter.emittingTime
    if (ms <= 0L) return "Emitter(0L).perSecond(${(1f / party.emitter.amountPerMs).roundToInt()})"
    val duration = if (ms % 1000 == 0L) "${ms / 1000}.seconds" else "$ms.milliseconds"
    return "Emitter($duration).max(${party.particleCount()})"
}

private fun rotationCode(rotation: Rotation) =
    if (rotation.enabled) "Rotation(speed = ${floatCode(rotation.speed)})" else "Rotation.disabled()"

private fun angleName(angle: Int) =
    when (angle) {
        Angle.TOP -> "Angle.TOP"
        Angle.BOTTOM -> "Angle.BOTTOM"
        Angle.LEFT -> "Angle.LEFT"
        Angle.RIGHT -> "Angle.RIGHT"
        else -> "$angle"
    }

private fun floatCode(value: Float) = if (value == value.toInt().toFloat()) "${value.toInt()}f" else "${value}f"

private fun colorCode(color: Int) = "0x" + color.toString(16).padStart(6, '0')

private fun sizeCode(size: Size) =
    when (size) {
        Size.SMALL -> "Size.SMALL"
        Size.MEDIUM -> "Size.MEDIUM"
        Size.LARGE -> "Size.LARGE"
        else -> "Size(${size.sizeInDp})"
    }

private fun shapeCode(shape: Shape) =
    when (shape) {
        Shape.Square -> "Shape.Square"
        Shape.Circle -> "Shape.Circle"
        is Shape.Rectangle -> "Shape.Rectangle(${floatCode(shape.heightRatio)})"
        Shape.Polygon.STAR -> "Shape.Polygon.STAR"
        Shape.Polygon.TRIANGLE -> "Shape.Polygon.TRIANGLE"
        Shape.Polygon.DIAMOND -> "Shape.Polygon.DIAMOND"
        is Shape.Polygon -> "Shape.Polygon.star()"
        is Shape.DrawableShape -> if (shape.image is PainterImage) "imageShape(heart)" else "drawableShape"
    }

private fun positionCode(position: Position): String =
    when (position) {
        is Position.Relative -> "Position.Relative(${position.x}, ${position.y})"
        is Position.Absolute -> "Position.Absolute(${floatCode(position.x)}, ${floatCode(position.y)})"
        is Position.Between -> "${positionCode(position.min)}.between(${positionCode(position.max)})"
    }
