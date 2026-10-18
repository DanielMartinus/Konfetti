package nl.dionsegijn.xml.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.rememberKonfettiState
import nl.dionsegijn.konfetti.compose.rememberReduceMotion
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.xml.image.ImageUtil
import nl.dionsegijn.samples.shared.Presets
import nl.dionsegijn.xml.compose.ui.theme.KonfettiTheme

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KonfettiTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    KonfettiUI()
                }
            }
        }
    }
}

@Composable
fun KonfettiUI() {
    val konfetti = rememberKonfettiState()
    // Respect "Remove animations" in the accessibility settings by skipping the confetti
    val reduceMotion = rememberReduceMotion()
    val celebrate: (List<Party>) -> Unit = { if (!reduceMotion) konfetti.start(it) }
    val context = LocalContext.current
    // Load the heart once, not on every recomposition
    val heart =
        remember(context) {
            ImageUtil.loadDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_heart)!!)
        }

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
            Text(text = if (reduceMotion) "Remove animations is on, so no confetti" else "Or tap anywhere")
        }
        KonfettiView(
            state = konfetti,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
