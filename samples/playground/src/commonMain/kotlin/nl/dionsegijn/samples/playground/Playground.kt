package nl.dionsegijn.samples.playground

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.withContext
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.CoreRectImpl

/** A named set of parties, like the presets in the samples */
data class Preset(
    val name: String,
    val parties: List<Party>,
)

private enum class Sheet { Tweak, Code }

/**
 * Plays presets on a timeline you can scrub, and lets you change them while they play.
 * Built for phones first: confetti fills the screen, the controls float in a card at the bottom.
 */
@Composable
fun Playground(
    presets: List<Preset>,
    modifier: Modifier = Modifier,
    initial: Preset = presets.first(),
    onClose: (() -> Unit)? = null,
) {
    var presetName by remember { mutableStateOf(initial.name) }
    val parties = remember { mutableStateListOf<Party>().apply { addAll(initial.parties) } }
    var timeMs by remember { mutableStateOf(0L) }
    var playing by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf(0) }
    var sheet by remember { mutableStateOf<Sheet?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current.density

    val current = parties.toList()
    val lengthMs = remember(current) { current.maxOf { it.endMs() } }
    val area by remember { derivedStateOf { CoreRectImpl(0f, 0f, canvasSize.width.toFloat(), canvasSize.height.toFloat()) } }
    var replay by remember { mutableStateOf(Replay(emptyList())) }
    var counts by remember { mutableStateOf(emptyList<Int>()) }

    // Replaying takes too long for one frame, so edits build off the main thread while the old
    // replay keeps playing. Edits that come in during a build are skipped, the next build has the latest
    LaunchedEffect(density) {
        snapshotFlow { parties.toList() to area }
            .conflate()
            .collect { (edited, editedArea) ->
                val editedLengthMs = edited.maxOf { it.endMs() }
                val startMs = timeMs.coerceAtMost(editedLengthMs)
                val previous = replay
                val (next, nextCounts) =
                    withContext(Dispatchers.Default) {
                        val next = previous.edit(edited, editedArea, density, startMs)
                        next to next.particleCounts(editedLengthMs)
                    }
                replay = next
                counts = nextCounts
            }
    }

    fun load(preset: Preset) {
        presetName = preset.name
        parties.clear()
        parties.addAll(preset.parties)
        selected = 0
        timeMs = 0L
        playing = true
    }

    // Move the playhead with the frame clock and loop at the end
    LaunchedEffect(playing, lengthMs) {
        if (!playing) return@LaunchedEffect
        var previous = 0L
        while (true) {
            withFrameMillis { frame ->
                if (previous != 0L) timeMs = (timeMs + (frame - previous).coerceAtMost(MAX_FRAME_MS)) % lengthMs
                previous = frame
            }
        }
    }

    Box(modifier.fillMaxSize().background(Palette.paper)) {
        DotGrid()
        ParticleCanvas(
            particles = { replay.particlesAt(timeMs.coerceAtMost(lengthMs)) },
            modifier = Modifier.fillMaxSize().onSizeChanged { canvasSize = it },
        )
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            TopBar(onClose)
            PresetPicker(presets, presetName, onPick = ::load, Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.weight(1f))
            Card {
                SheetHandle(
                    onOpen = { if (sheet == null) sheet = Sheet.Tweak },
                    onClose = { sheet = null },
                )
                Timeline(
                    parties = current,
                    counts = counts,
                    timeMs = timeMs.coerceAtMost(lengthMs),
                    lengthMs = lengthMs,
                    playing = playing,
                    selected = selected,
                    meta = "$presetName · ${current.size} lane${if (current.size == 1) "" else "s"}",
                    onPlayPause = { playing = !playing },
                    onSeek = {
                        playing = false
                        timeMs = it
                    },
                    onSelect = {
                        selected = it
                        if (sheet == null) sheet = Sheet.Tweak
                    },
                )
                Spacer(Modifier.height(14.dp))
                SheetTabs(
                    open = sheet,
                    lane = selected + 1,
                    onPick = { sheet = if (sheet == it) null else it },
                    onClose = { sheet = null },
                )
                when (sheet) {
                    Sheet.Tweak ->
                        PartyEditor(
                            party = current[selected.coerceIn(current.indices)],
                            onChange = { parties[selected.coerceIn(current.indices)] = it },
                            modifier = Modifier.padding(top = 12.dp).heightIn(max = 260.dp),
                        )
                    Sheet.Code -> CodeBox(partyCode(current), Modifier.padding(top = 12.dp))
                    null -> Unit
                }
            }
        }
    }
}

@Composable
private fun TopBar(onClose: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onClose != null) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(Palette.paper, CircleShape)
                    .border(1.dp, Palette.line, CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Text("✕", fontSize = 14.sp, color = Palette.ink)
            }
            Spacer(Modifier.width(12.dp))
        }
        LogoMark()
        Spacer(Modifier.width(8.dp))
        Text("Playground", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Palette.ink, letterSpacing = (-0.4).sp)
        Spacer(Modifier.weight(1f))
        FpsCounter()
    }
}

// Frames per second over the last half second, to spot slow frames while tweaking
@Composable
private fun FpsCounter() {
    var fps by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        var frames = 0
        var start = 0L
        while (true) {
            withFrameNanos { now ->
                if (start == 0L) start = now
                frames++
                if (now - start >= FPS_WINDOW_NANOS) {
                    fps = (frames * 1_000_000_000L / (now - start)).toInt()
                    frames = 0
                    start = now
                }
            }
        }
    }
    Text(
        "$fps FPS",
        fontFamily = Mono,
        fontSize = 11.sp,
        color = if (fps in 1 until SLOW_FPS) Palette.accent else Palette.muted,
        letterSpacing = 1.sp,
    )
}

// A pill with one segment per preset, the chosen one raised in white
@Composable
private fun PresetPicker(
    presets: List<Preset>,
    current: String,
    onPick: (Preset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .padding(horizontal = 16.dp)
            .background(Palette.soft, RoundedCornerShape(50))
            .horizontalScroll(rememberScrollState())
            .padding(3.dp),
    ) {
        presets.forEach { preset ->
            val chosen = preset.name == current
            Text(
                preset.name,
                fontSize = 13.sp,
                fontWeight = if (chosen) FontWeight.SemiBold else FontWeight.Medium,
                color = if (chosen) Palette.ink else Palette.muted,
                modifier =
                    Modifier
                        .then(
                            if (chosen) {
                                Modifier.shadow(
                                    1.dp,
                                    RoundedCornerShape(50),
                                ).background(Palette.paper, RoundedCornerShape(50))
                            } else {
                                Modifier
                            },
                        )
                        .clickable { onPick(preset) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

// A grab bar on top of the card: swipe down to close the sheet, swipe up to open it
@Composable
private fun SheetHandle(
    onOpen: () -> Unit,
    onClose: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                var dragged = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragged = 0f },
                    onDragEnd = {
                        if (dragged > SWIPE_DP.dp.toPx()) onClose()
                        if (dragged < -SWIPE_DP.dp.toPx()) onOpen()
                    },
                ) { _, amount -> dragged += amount }
            }.padding(bottom = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(width = 36.dp, height = 4.dp).background(Palette.line, RoundedCornerShape(2.dp)))
    }
}

// Tweak and Code open below the timeline. Tap the open one again, the chevron, or swipe down to close it
@Composable
private fun SheetTabs(
    open: Sheet?,
    lane: Int,
    onPick: (Sheet) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().background(Palette.soft, RoundedCornerShape(10.dp)).padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Sheet.entries.forEach { sheet ->
            val chosen = sheet == open
            Box(
                Modifier
                    .weight(1f)
                    .then(
                        if (chosen) {
                            Modifier.shadow(
                                1.dp,
                                RoundedCornerShape(7.dp),
                            ).background(Palette.paper, RoundedCornerShape(7.dp))
                        } else {
                            Modifier
                        },
                    )
                    .clickable { onPick(sheet) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (sheet == Sheet.Tweak) "Tweak lane $lane" else "Code",
                    fontSize = 13.sp,
                    fontWeight = if (chosen) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (chosen) Palette.ink else Palette.muted,
                )
            }
        }
        if (open != null) {
            Box(
                Modifier
                    .size(34.dp)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Chevron()
            }
        }
    }
}

// Drawn instead of a "⌄", which the web build has no font for
@Composable
private fun Chevron() {
    Canvas(Modifier.size(width = 12.dp, height = 7.dp)) {
        val stroke = 1.75.dp.toPx()
        drawLine(Palette.ink, Offset(stroke, stroke), Offset(size.width / 2, size.height - stroke), stroke, StrokeCap.Round)
        drawLine(Palette.ink, Offset(size.width / 2, size.height - stroke), Offset(size.width - stroke, stroke), stroke, StrokeCap.Round)
    }
}

@Composable
private fun CodeBox(
    code: String,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember(code) { mutableStateOf(false) }
    Box(modifier.fillMaxWidth().heightIn(max = 260.dp).background(Palette.soft, RoundedCornerShape(10.dp))) {
        Text(
            code,
            fontFamily = Mono,
            fontSize = 11.5.sp,
            lineHeight = 18.sp,
            color = Palette.ink,
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .padding(14.dp),
        )
        Text(
            if (copied) "Copied" else "Copy",
            fontFamily = Mono,
            fontSize = 12.sp,
            color = Palette.paper,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Palette.ink, RoundedCornerShape(8.dp))
                    .clickable {
                        clipboard.setText(AnnotatedString(code))
                        copied = true
                    }.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

// White card floating over the confetti, like the panels on konfetti.dev
@Composable
private fun Card(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .shadow(24.dp, shape, ambientColor = Palette.ink, spotColor = Palette.ink)
            .background(Palette.paper.copy(alpha = 0.96f), shape)
            .border(1.dp, Palette.line, shape)
            .padding(16.dp)
            .animateContentSize(),
    ) {
        content()
    }
}

// The dotted paper behind the confetti
@Composable
private fun DotGrid() {
    Canvas(Modifier.fillMaxSize()) {
        val step = 28.dp.toPx()
        val radius = 1.dp.toPx()
        var y = step / 2
        while (y < size.height) {
            var x = step / 2
            while (x < size.width) {
                drawCircle(Palette.dot, radius, Offset(x, y), alpha = 0.55f)
                x += step
            }
            y += step
        }
    }
}

private const val FPS_WINDOW_NANOS = 500_000_000L

// Below this the counter turns pink
private const val SLOW_FPS = 50

// How far the handle has to move before the sheet opens or closes
private const val SWIPE_DP = 24

// A longer gap, like the app coming back from the background, moves the playhead one short step
private const val MAX_FRAME_MS = 100L
