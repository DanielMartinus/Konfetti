package nl.dionsegijn.demo_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.dionsegijn.demo_compose.ui.theme.KonfettiTheme
import nl.dionsegijn.konfetti_compose.KonfettiView
import nl.dionsegijn.konfetti_compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti_core.ParticleSystem

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
fun KonfettiUI(viewModel: KonfettiViewModel = KonfettiViewModel()) {

    val state: KonfettiViewModel.State by viewModel.state.observeAsState(
        KonfettiViewModel.State.Idle
    )

    when (val newState = state) {
        KonfettiViewModel.State.Idle -> {
            Button(onClick = { viewModel.start() }) {
                Text(
                    text = "Start",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        is KonfettiViewModel.State.Started -> KonfettiView(
            Modifier.fillMaxSize(),
            particleSystem = newState.particleSystem,
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemStarted(system: ParticleSystem, activeSystems: Int) {

                }

                override fun onParticleSystemEnded(system: ParticleSystem, activeSystems: Int) {
                    if (activeSystems == 0) viewModel.ended()
                }
            }
        )
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KonfettiTheme {
        Greeting("Android")
    }
}
