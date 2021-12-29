package nl.dionsegijn.konfetti.compose

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem

@Composable
fun KonfettiView(
    modifier: Modifier = Modifier,
    parties: List<Party>,
    updateListener: OnParticleSystemUpdateListener? = null
) {

    lateinit var partySystems: List<PartySystem>

    /**
     * Particles to draw
     */
    val particles = remember { mutableStateOf(emptyList<Particle>()) }

    /**
     * Latest stored frameTimeMilliseconds
     */
    val frameTime = remember { mutableStateOf(0L) }

    /**
     * Area in which the particles are being drawn
     */
    val drawArea = remember { mutableStateOf(Rect()) }

    LaunchedEffect(Unit) {
        partySystems = parties.map { PartySystem(it) }
        while (true) {
            withFrameMillis { frameMs ->
                // Calculate time between frames, fallback to 0 when previous frame doesn't exist
                val deltaMs = if (frameTime.value > 0) (frameMs - frameTime.value) else 0
                frameTime.value = frameMs

                particles.value = partySystems.map { particleSystem ->

                    val totalTimeRunning = getTotalTimeRunning(particleSystem.createdAt)
                    // Do not start particleSystem yet if totalTimeRunning is below delay
                    if (totalTimeRunning < particleSystem.party.delay) return@map listOf()

                    if (particleSystem.isDoneEmitting()) {
                        updateListener?.onParticleSystemEnded(
                            system = particleSystem,
                            activeSystems = partySystems.count { !it.isDoneEmitting() }
                        )
                    }

                    particleSystem.render(deltaMs.div(1000f), drawArea.value)
                }.flatten()
            }
        }
    }

    Canvas(
        modifier = modifier
            .onGloballyPositioned {
                drawArea.value = Rect(0, 0, it.size.width, it.size.height)
            },
        onDraw = {
            particles.value.forEach { particle ->
                withTransform({
                    rotate(
                        degrees = particle.rotation,
                        pivot = Offset(
                            x = particle.x + (particle.width / 2),
                            y = particle.y + (particle.height / 2)
                        )
                    )
                    scale(
                        scaleX = particle.scaleX,
                        scaleY = 1f,
                        pivot = Offset(particle.x + (particle.width / 2), particle.y)
                    )
                }) {
                    particle.shape.draw(this, particle)
                }
            }
        }
    )
}

fun getTotalTimeRunning(startTime: Long): Long {
    val currentTime = System.currentTimeMillis()
    return (currentTime - startTime)
}
