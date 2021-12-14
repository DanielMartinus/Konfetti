package nl.dionsegijn.demo_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nl.dionsegijn.demo_compose.ui.theme.KonfettiTheme
import nl.dionsegijn.konfetti_compose.KonfettiView
import nl.dionsegijn.konfetti_compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti_core.PartySystem

class ComposeActivity : ComponentActivity() {

    private val viewModel by viewModels<KonfettiViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KonfettiTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    KonfettiUI(viewModel)
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
    val drawable = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_heart)
    when (val newState = state) {
        KonfettiViewModel.State.Idle -> {
            Button(onClick = { viewModel.start(drawable!!) }) {
                Text(
                    text = "Start",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        is KonfettiViewModel.State.Started -> KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = newState.party,
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                    if (activeSystems == 0) viewModel.ended()
                }
            }
        )
    }
}
