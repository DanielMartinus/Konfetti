package nl.dionsegijn.konfetti_compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import nl.dionsegijn.konfetti_compose.ui.theme.yellow
import nl.dionsegijn.konfetti_core.ParticleSystem
import nl.dionsegijn.konfetti_core.models.Shape
import kotlin.math.abs

@Composable
fun KonfettiView(modifier: Modifier) {

    val millis by startTimerIntegration(
        ParticleSystem()
            .setDirection(0.0, 359.0)
            .addColors(0xb48def, 0xf4306d, 0xfce18a, 0xff726d)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(18000L)
            .addShapes(Shape.Square, Shape.Circle)
            .setPosition(400f, 400f)
            .streamMaxParticles(20, 2000)
    )

    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            millis.forEach{ particle ->
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
//            withTransform({
////                translate(100f - 50f, 100f)
//                rotate(0f, Offset(100f + (100f / 2), 100f + (100f / 2)))
//                scale(0.5f, 1f, Offset(50f, 100f))
//            }) {
//                drawRect(
//                    color = Color.Red,
//                    topLeft = Offset(100f, 100f),
//                    size = Size(100f, 100f),
//                )
//            }
        }
    )
}
