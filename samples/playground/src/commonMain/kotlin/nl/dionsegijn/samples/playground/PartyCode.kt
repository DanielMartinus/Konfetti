package nl.dionsegijn.samples.playground

import nl.dionsegijn.konfetti.compose.PainterImage
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Drift
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
        if (drift != Drift()) fields += "drift = ${driftCode(drift)}"
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
    val duration =
        when {
            ms <= 0L -> "Duration.ZERO"
            ms % 1000 == 0L -> "${ms / 1000}.seconds"
            else -> "$ms.milliseconds"
        }
    val amount = if (party.emitter.isPerSecond) "perSecond(${party.emitter.amount})" else "max(${party.emitter.amount})"
    return "Emitter($duration).$amount"
}

private fun rotationCode(rotation: Rotation): String {
    if (rotation == Rotation.disabled()) return "Rotation.disabled()"
    val defaults = Rotation()
    val fields = mutableListOf<String>()
    with(rotation) {
        if (enabled != defaults.enabled) fields += "enabled = $enabled"
        if (speed != defaults.speed) fields += "speed = ${floatCode(speed)}"
        if (variance != defaults.variance) fields += "variance = ${floatCode(variance)}"
        if (multiplier2D != defaults.multiplier2D) fields += "multiplier2D = ${floatCode(multiplier2D)}"
        if (multiplier3D != defaults.multiplier3D) fields += "multiplier3D = ${floatCode(multiplier3D)}"
        if (faceDirection) fields += "faceDirection = true"
    }
    return "Rotation(${fields.joinToString()})"
}

private fun driftCode(drift: Drift): String {
    if (drift == Drift.enabled()) return "Drift.enabled()"
    val defaults = Drift()
    val fields = mutableListOf<String>()
    with(drift) {
        if (enabled != defaults.enabled) fields += "enabled = $enabled"
        if (sway != defaults.sway) fields += "sway = ${floatCode(sway)}"
        if (frequency != defaults.frequency) fields += "frequency = ${floatCode(frequency)}"
        if (variance != defaults.variance) fields += "variance = ${floatCode(variance)}"
        if (tilt != defaults.tilt) fields += "tilt = ${floatCode(tilt)}"
        if (drag != defaults.drag) fields += "drag = ${floatCode(drag)}"
    }
    return "Drift(${fields.joinToString()})"
}

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
        Size(size.sizeInDp) -> "Size(${size.sizeInDp})"
        else -> "Size(${size.sizeInDp}, ${floatCode(size.mass)}, ${floatCode(size.massVariance)})"
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
