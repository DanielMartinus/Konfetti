package nl.dionsegijn.konfetti_compose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
            val showKonfetti = mutableStateOf<State<List<Particle>>>(mutableStateOf(emptyList<Particle>()))
        }
    }
}

@Composable
fun MyApp(viewModel: KonfettiViewModel = KonfettiViewModel()) {

    val state: KonfettiViewModel.State by viewModel.state.observeAsState(KonfettiViewModel.State.Idle)

    when (val newState = state) {
        KonfettiViewModel.State.Idle -> { Button(onClick = {viewModel.start() }) {
            Text(
                text = "Start",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } }
        is KonfettiViewModel.State.Started -> KonfettiView(
            Modifier.fillMaxSize(),
            particleSystem = newState.particleSystem
        )
    }
}

//    val showKonfetti = mutableStateOf(emptyList<Particle>())
//
//@Composable
//fun show() {
//    showKonfetti.value = startTimerIntegration(
//        ParticleSystem()
//            .setDirection(0.0, 359.0)
//            .addColors(0xb48def, 0xf4306d, 0xfce18a, 0xff726d)
//            .setSpeed(1f, 5f)
//            .setFadeOutEnabled(true)
//            .setTimeToLive(18000L)
//            .addShapes(Shape.Square, Shape.Circle)
//            .setPosition(400f, 400f)
//            .streamMaxParticles(20, 2000)
//    ).value
//}
