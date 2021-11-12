package nl.dionsegijn.konfetti_compose

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import nl.dionsegijn.konfetti_core.ParticleSystem

@Composable
fun KonfettiView(
    modifier: Modifier,
    particleSystem: List<ParticleSystem>,
    updateListener: OnParticleSystemUpdateListener? = null
) {

    val drawArea = remember { mutableStateOf(Rect()) }
    val particles by runKonfetti(
        drawArea,
        particleSystem,
        updateListener
    )

    Canvas(
        modifier = modifier
            .onGloballyPositioned {
                drawArea.value = Rect(0, 0, it.size.width, it.size.height)
            },
        onDraw = {
            particles.forEach { particle ->
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
