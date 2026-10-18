package nl.dionsegijn.samples.multiplatform

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.imageShape
import nl.dionsegijn.konfetti.compose.rememberKonfettiState
import nl.dionsegijn.konfetti.compose.rememberReduceMotion
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.samples.multiplatform.resources.Res
import nl.dionsegijn.samples.multiplatform.resources.heart
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    MaterialTheme {
        Surface(color = MaterialTheme.colors.background) {
            KonfettiUI()
        }
    }
}

@Composable
private fun KonfettiUI() {
    val konfetti = rememberKonfettiState()
    // Respect the system's reduce motion setting by skipping the confetti
    val reduceMotion = rememberReduceMotion()
    val celebrate: (List<Party>) -> Unit = { if (!reduceMotion) konfetti.start(it) }
    val heart = imageShape(painterResource(Res.drawable.heart))

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                // Tap anywhere next to the buttons for a burst at that spot
                .pointerInput(Unit) {
                    detectTapGestures { if (!reduceMotion) konfetti.burst(at = it, party = Presets.explode().first()) }
                },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { celebrate(Presets.festive(heart)) }) {
                Text(text = "Festive")
            }
            Button(onClick = { celebrate(Presets.explode()) }) {
                Text(text = "Explode")
            }
            Button(onClick = { celebrate(Presets.parade()) }) {
                Text(text = "Parade")
            }
            Button(onClick = { celebrate(Presets.rain()) }) {
                Text(text = "Rain")
            }
            Button(onClick = { celebrate(Presets.shapes()) }) {
                Text(text = "Shapes")
            }
            Button(onClick = { konfetti.stopGracefully() }) {
                Text(text = "Stop")
            }
            Text(text = if (reduceMotion) "Reduce motion is on, so no confetti" else "Or tap anywhere")
        }
        KonfettiView(
            state = konfetti,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
