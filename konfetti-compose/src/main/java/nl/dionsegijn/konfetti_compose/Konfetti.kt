package nl.dionsegijn.konfetti_compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import nl.dionsegijn.konfetti_core.ParticleSystem

@Composable
fun KonfettiView(modifier: Modifier, particleSystem: ParticleSystem, updateListener: OnParticleSystemUpdateListener? = null) {

    val millis by runKonfetti(
        particleSystem,
        updateListener
    )

    // TODO: support all canvas operations
    // TODO: Support drawing custom shapes
    Canvas(
        modifier = Modifier.fillMaxSize().clickable { },
        onDraw = {
            millis.forEach { particle ->
                withTransform({
                    rotate(particle.rotation, Offset(particle.x + (particle.width / 2), particle.y + (particle.height / 2)))
                    scale(particle.scaleX, 1f, Offset(particle.x + (particle.width / 2), particle.y))
                }) {
                    drawRect(
                        color = Color(particle.color),
                        topLeft = Offset(particle.x, particle.y),
                        size = Size(particle.width, particle.height),
                    )
                }
            }
        }
    )
}
