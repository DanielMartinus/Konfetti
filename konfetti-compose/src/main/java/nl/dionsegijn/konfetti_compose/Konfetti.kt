package nl.dionsegijn.konfetti_compose

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import nl.dionsegijn.konfetti_compose.ui.theme.yellow

@Composable
fun KonfettiView(modifier: Modifier) {

    val millis by startTimerIntegration()

    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {

//            Log.e("DION", "DION time: [${millis}] & dt: ${millis}")
            val particle = millis[0]
            drawRect(
                color = yellow,
                topLeft = Offset(particle.x - (particle.width / 2), particle.y),
                size = Size(particle.width, 100f)
            )
        }
    )
}
