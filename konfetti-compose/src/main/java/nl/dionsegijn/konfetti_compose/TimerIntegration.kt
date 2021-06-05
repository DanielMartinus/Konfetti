package nl.dionsegijn.konfetti_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameMillis
import nl.dionsegijn.konfetti_core.ParticleSystem
import kotlin.math.abs

@Composable
fun startTimerIntegration(particleSystem: ParticleSystem): State<List<Particle>> {
    val particles = mutableStateOf(emptyList<Particle>())
    val durationMs = mutableStateOf(0L)
    LaunchedEffect(true) {
        val startTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameTime ->
                val old = durationMs.value
                val newValue = frameTime - startTime
                durationMs.value = frameTime - startTime

                particleSystem.renderSystem.render(16f / 1000)
                particles.value = particleSystem.renderSystem.particles.map {
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
