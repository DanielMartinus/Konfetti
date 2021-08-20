package nl.dionsegijn.demo_compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.dionsegijn.konfetti_core.ParticleSystem
import nl.dionsegijn.konfetti_core.models.Shape

class KonfettiViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun start() {
        _state.value = State.Started(
            ParticleSystem()
                .setDirection(0.0, 359.0)
                .addColors(0xb48def, 0xf4306d, 0xfce18a, 0xff726d)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(10000L)
                .addShapes(Shape.Square, Shape.Circle)
                .setPosition(400f, 400f)
                .streamMaxParticles(10, 40)
        )
    }

    fun ended() {
        _state.value = State.Idle
    }

    sealed class State {
        class Started(val particleSystem: ParticleSystem) : State()
        object Idle : State()
    }
}
