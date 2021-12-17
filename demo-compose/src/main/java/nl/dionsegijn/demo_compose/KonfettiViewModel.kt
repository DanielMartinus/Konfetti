package nl.dionsegijn.demo_compose

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.dionsegijn.konfetti_core.Angle
import nl.dionsegijn.konfetti_core.NewEmitter.Emitter
import nl.dionsegijn.konfetti_core.Party
import nl.dionsegijn.konfetti_core.Position
import nl.dionsegijn.konfetti_core.Spread
import nl.dionsegijn.konfetti_core.Velocity
import java.util.concurrent.TimeUnit

class KonfettiViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun start(drawable: Drawable) {
        val party = Party(
            velocity = Velocity(5f, 7f),
            angle = Angle.TOP,
            spread = Spread.SMALL,
            timeToLive = 3000L,
            colors = listOf(0xfce18a, 0xff726d),
            emitter = Emitter(duration = 150, TimeUnit.MILLISECONDS).max(50),
            position = Position.relative(0.5, 0.5)
        )

        _state.value = State.Started(
            listOf(
                party,
                party.copy(
                    spread = 40,
                    colors = listOf(0xf4306d, 0xb48def),
                    velocity = Velocity(4f, 8f)
                )
            )
        )
    }

    fun ended() {
        _state.value = State.Idle
    }

    sealed class State {
        class Started(val party: List<Party>) : State()
        object Idle : State()
    }
}
