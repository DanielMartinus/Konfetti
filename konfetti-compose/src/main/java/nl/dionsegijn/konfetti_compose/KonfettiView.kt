package nl.dionsegijn.konfetti_compose

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
import nl.dionsegijn.konfetti_core.Confetti
import nl.dionsegijn.konfetti_core.ParticleSystem
import nl.dionsegijn.konfetti_core.models.Shape
import kotlin.math.abs

@Composable
fun KonfettiView(
    modifier: Modifier = Modifier,
    particleSystems: List<ParticleSystem>,
    updateListener: OnParticleSystemUpdateListener? = null
) {
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
        while (true) {
            withFrameMillis { frameMs ->
                // Calculate time between frames, fallback to 0 when previous frame doesn't exist
                val deltaMs = if (frameTime.value > 0) (frameMs - frameTime.value) else 0
                frameTime.value = frameMs

                particles.value = particleSystems.map { particleSystem ->

                    val totalTimeRunning = getTotalTimeRunning(particleSystem.renderSystem.createdAt)
                    // Do not start particleSystem yet if totalTimeRunning is below delay
                    if (totalTimeRunning < particleSystem.getDelay()) return@map listOf()

                    particleSystem.renderSystem.render(deltaMs.div(1000f), drawArea.value)

                    if (particleSystem.doneEmitting()) {
                        updateListener?.onParticleSystemEnded(
                            system = particleSystem,
                            activeSystems = particleSystems.count { !it.doneEmitting() })
                    }

                    particleSystem.renderSystem.getDrawableParticles().map {
                        it.toParticle()
                    }
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

fun Confetti.toParticle(): Particle {
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
