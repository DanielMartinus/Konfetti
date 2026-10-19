@file:OptIn(ExperimentalJsExport::class)

package nl.dionsegijn.konfetti.web

import nl.dionsegijn.konfetti.core.Replay
import nl.dionsegijn.konfetti.core.endMs
import nl.dionsegijn.konfetti.core.models.CoreImage
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.startMs
import nl.dionsegijn.konfetti.json.KonfettiJson

/**
 * Plays parties from a Konfetti json file on one screen and draws any moment of it.
 * The same time always shows the same confetti, so a timeline can scrub back and forth.
 *
 * ```
 * const engine = new KonfettiEngine()
 * engine.load(json, 393, 852, 3)
 * engine.draw(context, 1000, 1)
 * ```
 */
@JsExport
class KonfettiEngine {
    private var replay = Replay(emptyList())
    private val images = mutableMapOf<String, WebImage>()
    private val painter = Painter()

    /** Number of parties loaded */
    val partyCount: Int get() = replay.lanes.size

    /**
     * Registers an image for image shapes with this [id].
     * @param source anything canvas can draw, like an HTMLImageElement or ImageBitmap
     */
    fun registerImage(
        id: String,
        source: dynamic,
        width: Int,
        height: Int,
    ) {
        images[id] = WebImage(id, width, height, source)
        painter.forget(id)
    }

    /**
     * Loads the parties for a screen of [widthDp] by [heightDp] at [density] pixels per dp.
     * Parties that didn't change since the last load keep playing, so editing one stays smooth.
     * @param timeMs the moment the screen shows now, changed parties are played up to it
     * @throws Throwable with a readable message when the json isn't valid
     */
    fun load(
        json: String,
        widthDp: Double,
        heightDp: Double,
        density: Double,
        timeMs: Double = 0.0,
    ) {
        val parties = KonfettiJson.decode(json, density.toFloat()) { images[it] }
        val drawArea = CoreRectImpl(0f, 0f, (widthDp * density).toFloat(), (heightDp * density).toFloat())
        replay = replay.edit(parties, drawArea, density.toFloat(), timeMs.toLong())
    }

    /** Where party [index] starts on the timeline */
    fun startMs(index: Int): Double = replay.lanes[index].party.startMs().toDouble()

    /** Where party [index] ends on the timeline, after its last confetti faded out */
    fun endMs(index: Int): Double = replay.lanes[index].party.endMs().toDouble()

    /** Where the last party ends */
    fun lengthMs(): Double = replay.lanes.maxOfOrNull { it.party.endMs() }?.toDouble() ?: 0.0

    /** How many confetti are on screen at [timeMs] */
    fun particleCount(timeMs: Double): Int = replay.particlesAt(timeMs.toLong()).size

    /** How many confetti are on screen every [Replay.STEP_MS] until [timeMs], for a chart */
    fun particleCounts(timeMs: Double): Array<Int> = replay.particleCounts(timeMs.toLong()).toTypedArray()

    /**
     * Draws the confetti at [timeMs] on a 2D canvas context.
     * @param scale canvas pixels per screen pixel, for zoom and the device pixel ratio
     * @param hidden parties to leave out, by index
     */
    fun draw(
        context: dynamic,
        timeMs: Double,
        scale: Double,
        hidden: Array<Int> = emptyArray(),
    ) {
        context.save()
        context.scale(scale, scale)
        replay.lanes.forEachIndexed { i, lane ->
            // Hidden parties still play, so showing them again doesn't jump
            val particles = lane.particlesAt(timeMs.toLong())
            if (i !in hidden) painter.draw(context, particles)
        }
        context.restore()
    }
}

/** An image registered for image shapes. Equal by id, so reloading the same json keeps the parties. */
internal class WebImage(
    val id: String,
    override val width: Int,
    override val height: Int,
    val source: dynamic,
) : CoreImage {
    override fun equals(other: Any?): Boolean = other is WebImage && other.id == id && other.width == width && other.height == height

    override fun hashCode(): Int = id.hashCode()
}
