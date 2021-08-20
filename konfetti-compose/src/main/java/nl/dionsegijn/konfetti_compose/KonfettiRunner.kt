package nl.dionsegijn.konfetti_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.unit.IntSize
import nl.dionsegijn.konfetti_core.ParticleSystem
import kotlin.math.abs

@Composable
fun runKonfetti(
    particleSystem: ParticleSystem,
    size: MutableState<IntSize>,
    updateListener: OnParticleSystemUpdateListener? = null
): State<List<Particle>> {
    val particles = remember { mutableStateOf(emptyList<Particle>()) }
    val durationMs = remember { mutableStateOf(0L) }

    LaunchedEffect(true) {
        val startTime = withFrameMillis { it }

        while (true) {
            withFrameMillis { frameTime ->
                val old = durationMs.value
                val newValue = frameTime - startTime
                durationMs.value = frameTime - startTime

                // TODO: Use real delta time instead of the temporary 16ms
                particleSystem.renderSystem.render(16f / 1000)
                particles.value = particleSystem.renderSystem.particles.map {
                    // if the particle is outside the bottom of the view the lifetime is over.
                    if (it.location.y > size.value.height) {
                        it.lifespan = 0
                    }

                    val color = (it.alpha shl 24) or (it.color and 0xffffff)
                    val scaleX = abs(it.rotationWidth / it.width - 0.5f) * 2
                    Particle(
                        it.location.x,
                        it.location.y,
                        it.width,
                        it.width,
                        color,
                        it.rotation,
                        scaleX
                    )
                }

                if (particleSystem.doneEmitting()) {
                    updateListener?.onParticleSystemEnded(particleSystem, 0)
                }
            }
        }
    }
    return particles
}

data class Particle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int,
    val rotation: Float,
    val scaleX: Float
)
