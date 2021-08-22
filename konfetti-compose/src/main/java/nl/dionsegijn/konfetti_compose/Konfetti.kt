package nl.dionsegijn.konfetti_compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import nl.dionsegijn.konfetti_core.ParticleSystem

@Composable
fun KonfettiView(
    modifier: Modifier,
    particleSystem: List<ParticleSystem>,
    updateListener: OnParticleSystemUpdateListener? = null
) {

    val size = remember { mutableStateOf(IntSize.Zero) }
    val millis by runKonfetti(
        particleSystem,
        size,
        updateListener
    )

    RenderParticles(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size.value = it },
        particles = millis
    )
}

@Composable
private fun RenderParticles(modifier: Modifier, particles: List<Particle>) {
    // TODO: support all canvas operations
    // TODO: Support drawing custom shapes
    Canvas(
        modifier = modifier,
        onDraw = {
            particles.forEach { particle ->
                withTransform({
                    rotate(
                        particle.rotation,
                        Offset(
                            particle.x + (particle.width / 2),
                            particle.y + (particle.height / 2)
                        )
                    )
                    scale(
                        particle.scaleX,
                        1f,
                        Offset(particle.x + (particle.width / 2), particle.y)
                    )
                }) {
                    particle.shape.draw(this, particle)
                }
            }
        }
    )
}
