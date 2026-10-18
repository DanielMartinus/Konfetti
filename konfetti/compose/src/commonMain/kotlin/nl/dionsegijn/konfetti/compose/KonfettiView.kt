// Shares its JVM class with the Android image helpers, so existing callers keep working
@file:JvmName("KonfettiViewKt")
@file:JvmMultifileClass

package nl.dionsegijn.konfetti.compose

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.first
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.time.Clock

/**
 * Draws the confetti of [state]. Start parties with [KonfettiState.start] or [KonfettiState.burst].
 *
 * @param onPartyEnded Called once for each party that finishes, with the number of parties still running.
 */
@Composable
fun KonfettiView(
    state: KonfettiState,
    modifier: Modifier = Modifier,
    onPartyEnded: ((activeParties: Int) -> Unit)? = null,
) {
    val currentOnPartyEnded by rememberUpdatedState(onPartyEnded)
    KonfettiCanvas(state, modifier) { _, activeSystems -> currentOnPartyEnded?.invoke(activeSystems) }
}

@Composable
fun KonfettiView(
    modifier: Modifier = Modifier,
    parties: List<Party>,
    updateListener: OnParticleSystemUpdateListener? = null,
) {
    val state = rememberKonfettiState()

    // Start over whenever a different list of parties comes in
    LaunchedEffect(parties) {
        state.reset()
        state.start(parties)
    }

    // A running party calls the latest listener, not the one from the first composition
    val currentUpdateListener by rememberUpdatedState(updateListener)
    KonfettiCanvas(state, modifier) { system, activeSystems ->
        currentUpdateListener?.onParticleSystemEnded(system, activeSystems)
    }
}

@Composable
private fun KonfettiCanvas(
    state: KonfettiState,
    modifier: Modifier,
    onSystemEnded: (system: PartySystem, activeSystems: Int) -> Unit,
) {
    /**
     * Particles to draw
     */
    val particles = remember { mutableStateOf(emptyList<Particle>()) }

    /**
     * Latest stored frame time in milliseconds.
     * Cleared when the app resumes so a background pause is not applied as one frame.
     */
    val frameTime = remember { mutableStateOf(0L) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    frameTime.value = 0L
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    /**
     * Area in which the particles are being drawn
     */
    val drawArea = remember { mutableStateOf(CoreRectImpl()) }

    // The density of this composition, which follows display size and density overrides
    val pixelDensity by rememberUpdatedState(LocalDensity.current.density)

    val currentOnSystemEnded by rememberUpdatedState(onSystemEnded)
    val listener =
        remember {
            object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(
                    system: PartySystem,
                    activeSystems: Int,
                ) = currentOnSystemEnded(system, activeSystems)
            }
        }

    LaunchedEffect(state) {
        while (true) {
            // Wait for a party to start, so an idle view doesn't ask for frames
            snapshotFlow { state.pending.size }.first { it > 0 }
            // The gap since the last party would count as one long frame
            frameTime.value = 0L
            while (state.isRunning()) {
                withInfiniteAnimationFrameMillis { frameMs ->
                    state.startPending(pixelDensity)
                    val deltaMs = frameDeltaMillis(frameTime.value, frameMs)
                    frameTime.value = frameMs

                    particles.value = renderFrame(state.systems, deltaMs.div(1000f), drawArea.value, listener)
                }
            }
            particles.value = emptyList()
        }
    }

    Canvas(
        modifier =
            modifier
                .onGloballyPositioned {
                    drawArea.value =
                        CoreRectImpl(0f, 0f, it.size.width.toFloat(), it.size.height.toFloat())
                },
        onDraw = {
            particles.value.forEach { particle ->
                withTransform({
                    rotate(
                        degrees = particle.rotation,
                        pivot =
                            Offset(
                                x = particle.x + (particle.width / 2),
                                y = particle.y + (particle.height / 2),
                            ),
                    )
                    scale(
                        scaleX = particle.scaleX,
                        scaleY = 1f,
                        pivot = Offset(particle.x + (particle.width / 2), particle.y),
                    )
                }) {
                    particle.shape.drawShape(drawScope = this, particle = particle, imageStore = state.imageStore)
                }
            }
        },
    )
}

fun getTotalTimeRunning(startTime: Long): Long {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    return (currentTime - startTime)
}
