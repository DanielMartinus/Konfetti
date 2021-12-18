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
import nl.dionsegijn.konfetti_core.models.Size
import java.util.concurrent.TimeUnit

class KonfettiViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun start(drawable: Drawable) {
        val party = Party(
            velocity = Velocity(45f),
            damping = 0.9f,
            angle = Angle.TOP,
            spread = Spread.SMALL,
            size = listOf(Size.MEDIUM),
            timeToLive = 3000L,
            colors = listOf(0xfce18a, 0xff726d),
            emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(100),
            position = Position.relative(0.5, 1.0)
        )

        _state.value = State.Started(
            listOf(
                party,
//                party.copy(
//                    velocity = Velocity(40f, 50f),
//                    spread = Spread.SMALL - 5,
//                    colors = listOf(0xf4306d, 0xb48def),
//                )
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
