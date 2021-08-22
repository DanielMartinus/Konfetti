package nl.dionsegijn.konfetti_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.unit.IntSize
import nl.dionsegijn.konfetti_core.Confetti
import nl.dionsegijn.konfetti_core.ParticleSystem
import nl.dionsegijn.konfetti_core.models.Shape
import kotlin.math.abs

@Composable
fun runKonfetti(
    particleSystems: List<ParticleSystem>,
    size: MutableState<IntSize>,
    updateListener: OnParticleSystemUpdateListener? = null
): State<List<Particle>> {
    /**
     * Particles to draw
     */
    val particles = remember { mutableStateOf(emptyList<Particle>()) }

    /**
     * Latest stored frameTimeMilliseconds
     */
    val frameTime = remember { mutableStateOf(0L) }

    LaunchedEffect(true) {
        while (true) {
            withFrameMillis { frameMs ->
                // Calculate time between frames, fallback to 0 when previous frame doesn't exist
                val deltaMs = if (frameTime.value > 0) (frameMs - frameTime.value) else 0
                frameTime.value = frameMs

                val screenHeight = size.value.height
                particles.value = particleSystems.map { particleSystem ->

                    val totalTimeRunning = getTotalTimeRunning(particleSystem.renderSystem.createdAt)
                    // Do not start particleSystem yet if totalTimeRunning is below delay
                    if (totalTimeRunning < particleSystem.getDelay()) return@map listOf()

                    particleSystem.renderSystem.render(deltaMs.div(1000f))

                    if (particleSystem.doneEmitting()) {
                        updateListener?.onParticleSystemEnded(
                            system = particleSystem,
                            activeSystems = particleSystems.count { !it.doneEmitting() })
                    }

                    particleSystem.renderSystem.particles.map {
                        it.toParticle(screenHeight)
                    }
                }.flatten()
            }
        }
    }
    return particles
}

fun Confetti.toParticle(canvasHeight: Int): Particle {
    // if the particle is outside the bottom of the view the lifetime is over.
    if (location.y > canvasHeight) {
        lifespan = 0
    }

    val color = (alpha shl 24) or (color and 0xffffff)
    val scaleX = abs(rotationWidth / width - 0.5f) * 2
    return Particle(
        location.x,
        location.y,
        width,
        width,
        color,
        rotation,
        scaleX,
        shape,
        alpha
    )
}

fun getTotalTimeRunning(startTime: Long): Long {
    val currentTime = System.currentTimeMillis()
    return (currentTime - startTime)
}

data class Particle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int,
    val rotation: Float,
    val scaleX: Float,
    val shape: Shape,
    val alpha: Int
)
