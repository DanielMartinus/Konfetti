package nl.dionsegijn.samples.playground

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.dionsegijn.konfetti.core.Party

private val RULER_HEIGHT = 18.dp
private val LANE_HEIGHT = 18.dp
private val LANE_GAP = 4.dp
private val ROW_GAP = 8.dp
private val CHART_HEIGHT = 36.dp

/**
 * Play, pause and scrub, like the timeline on konfetti.dev. Each party gets a lane from its delay
 * until its last confetti fades out, and the curve below shows how many confetti are on screen.
 * Drag anywhere to scrub, tap a lane to edit that party.
 */
@Composable
internal fun Timeline(
    parties: List<Party>,
    counts: List<Int>,
    timeMs: Long,
    lengthMs: Long,
    playing: Boolean,
    selected: Int,
    meta: String,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayButton(playing, onPlayPause)
            Spacer(Modifier.width(10.dp))
            Text(seconds(timeMs), fontFamily = Mono, fontSize = 13.sp, color = Palette.ink)
            Text(" / ${seconds(lengthMs)}", fontFamily = Mono, fontSize = 13.sp, color = Palette.muted)
            Spacer(Modifier.weight(1f))
            Text(meta, fontFamily = Mono, fontSize = 11.sp, color = Palette.muted, maxLines = 1)
        }
        Spacer(Modifier.height(12.dp))
        Tracks(parties, counts, timeMs, lengthMs, selected, onSeek, onSelect)
    }
}

@Composable
private fun Tracks(
    parties: List<Party>,
    counts: List<Int>,
    timeMs: Long,
    lengthMs: Long,
    selected: Int,
    onSeek: (Long) -> Unit,
    onSelect: (Int) -> Unit,
) {
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .pointerInput(lengthMs, parties.size) {
                detectTapGestures { tap ->
                    val lanesTop = (RULER_HEIGHT + ROW_GAP).toPx()
                    val lane = ((tap.y - lanesTop) / (LANE_HEIGHT + LANE_GAP).toPx()).toInt()
                    if (tap.y >= lanesTop && lane in parties.indices) onSelect(lane) else onSeek(timeAt(tap.x, size.width, lengthMs))
                }
            }.pointerInput(lengthMs) {
                detectHorizontalDragGestures(onDragStart = { onSeek(timeAt(it.x, size.width, lengthMs)) }) { change, _ ->
                    onSeek(timeAt(change.position.x, size.width, lengthMs))
                }
            }.drawWithContent {
                drawContent()
                // Playhead: a pink line with a little tab on top
                val x = size.width * (timeMs.toFloat() / lengthMs).coerceIn(0f, 1f)
                drawLine(Palette.accent, Offset(x, 12.dp.toPx()), Offset(x, size.height), strokeWidth = 2.dp.toPx())
                drawRoundRect(
                    color = Palette.accent,
                    topLeft = Offset(x - 6.dp.toPx(), 2.dp.toPx()),
                    size = Size(12.dp.toPx(), 12.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                )
            },
    ) {
        val width = maxWidth
        Column(verticalArrangement = Arrangement.spacedBy(ROW_GAP)) {
            Ruler(lengthMs, width)
            Column(verticalArrangement = Arrangement.spacedBy(LANE_GAP)) {
                parties.forEachIndexed { i, party ->
                    val start = (party.startMs().toFloat() / lengthMs).coerceIn(0f, 1f)
                    val end = (party.endMs().toFloat() / lengthMs).coerceIn(start, 1f)
                    Box(Modifier.fillMaxWidth().height(LANE_HEIGHT).background(Palette.soft, RoundedCornerShape(5.dp))) {
                        Box(
                            Modifier
                                .offset(x = width * start)
                                .width(width * (end - start))
                                .fillMaxHeight()
                                .background(if (i == selected) Palette.accent else Palette.ink, RoundedCornerShape(4.dp))
                                .padding(start = 8.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                party.laneLabel(),
                                fontFamily = Mono,
                                fontSize = 10.sp,
                                color = Palette.paper,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                            )
                        }
                    }
                }
            }
            CountChart(counts)
        }
    }
}

@Composable
private fun Ruler(
    lengthMs: Long,
    width: androidx.compose.ui.unit.Dp,
) {
    Box(
        Modifier.fillMaxWidth().height(RULER_HEIGHT).drawWithContent {
            drawContent()
            drawLine(Palette.line, Offset(0f, size.height), Offset(size.width, size.height), 1.dp.toPx())
        },
    ) {
        var second = 0L
        while (second * 1000 < lengthMs) {
            val fraction = second * 1000f / lengthMs
            // Skip labels that would run off the end
            if (fraction < 0.92f) {
                Text(
                    "${second}s",
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    color = Palette.muted,
                    modifier = Modifier.offset(x = width * fraction),
                )
            }
            second++
        }
    }
}

// The number of confetti over time, filled with the confetti colors like on the website
@Composable
private fun CountChart(counts: List<Int>) {
    Canvas(Modifier.fillMaxWidth().height(CHART_HEIGHT).background(Palette.soft, RoundedCornerShape(6.dp))) {
        if (counts.size < 2) return@Canvas
        val max = counts.max().coerceAtLeast(1)
        val top = 6.dp.toPx()

        fun point(i: Int) =
            Offset(
                i.toFloat() / (counts.size - 1) * size.width,
                size.height - counts[i].toFloat() / max * (size.height - top),
            )
        val line =
            Path().apply {
                counts.indices.forEach {
                    if (it == 0) {
                        moveTo(
                            point(it).x,
                            point(it).y,
                        )
                    } else {
                        lineTo(point(it).x, point(it).y)
                    }
                }
            }
        val area =
            Path().apply {
                addPath(line)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        drawPath(area, Brush.horizontalGradient(Palette.confetti), alpha = 0.55f)
        drawPath(line, Palette.ink, style = Stroke(width = 1.25.dp.toPx()))
    }
}

@Composable
private fun PlayButton(
    playing: Boolean,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(36.dp)
            .background(Palette.paper, CircleShape)
            .border(1.dp, Palette.line, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(12.dp)) {
            if (playing) {
                val bar = Size(size.width / 4, size.height)
                drawRoundRect(Palette.ink, Offset(size.width / 8, 0f), bar, CornerRadius(2f))
                drawRoundRect(Palette.ink, Offset(size.width * 5 / 8, 0f), bar, CornerRadius(2f))
            } else {
                val triangle =
                    Path().apply {
                        moveTo(size.width * 0.2f, 0f)
                        lineTo(size.width, size.height / 2)
                        lineTo(size.width * 0.2f, size.height)
                        close()
                    }
                drawPath(triangle, Palette.ink)
            }
        }
    }
}

// Lane text, like "Burst · 30" or "5.0 s · 500"
private fun Party.laneLabel(): String {
    val ms = emitter.emittingTime
    return when {
        ms <= 0L -> "Endless"
        ms <= 150L -> "Burst · ${particleCount()}"
        else -> "${ms / 1000}.${ms % 1000 / 100} s · ${particleCount()}"
    }
}

private fun timeAt(
    x: Float,
    width: Int,
    lengthMs: Long,
) = ((x / width).coerceIn(0f, 1f) * lengthMs).toLong()

// Seconds with two decimals, like 1.25 s
internal fun seconds(ms: Long) = "${ms / 1000}.${(ms % 1000 / 10).toString().padStart(2, '0')} s"
