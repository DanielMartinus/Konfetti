package nl.dionsegijn.demo_compose

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.dionsegijn.konfetti_core.NewEmitter.Emitter
import nl.dionsegijn.konfetti_core.Party
import nl.dionsegijn.konfetti_core.Position
import java.util.concurrent.TimeUnit

class KonfettiViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun start(drawable: Drawable) {
        _state.value = State.Started(
            listOf(
                Party(
                    startVelocity = 5,
                    angle = 270, // TOP
                    spread = 30,
                    timeToLive = 3000L,
                    colors = listOf(0xfce18a, 0xff726d),
                    emitter = Emitter(duration = 1L, TimeUnit.SECONDS).max(300),
                    position = Position(400f, 600f)
                )
//                ParticleSystem()
//                    .setDirection(0.0, 359.0)
//                    .addColors()
//                    .setSpeed(1f, 6f)
//                    .addSizes(Size(8), Size(20), Size(2))
//                    .setFadeOutEnabled(true)
//                    .setTimeToLive(3000L)
//                    .addShapes(Shape.Circle, Shape.Rectangle(0.2f), Shape.DrawableShape(drawable))
//                    .setPosition(200f, 400f)
//                    .setRotationSpeedMultiplier(1.2f)
//                    .setRotationSpeedVariance(0.6f)
//                    .emitter(StreamEmitter(5, TimeUnit.SECONDS).max(500))
//                ParticleSystem()
//                    .setDirection(0.0, 359.0)
//                    .addColors(0xf4306d, 0xfce18a, 0xff726d, 0xb48def)
//                    .addSizes(Size(8), Size(20), Size(2))
//                    .setSpeed(1f, 15f)
//                    .setFadeOutEnabled(true)
//                    .setTimeToLive(5000L)
//                    .addShapes(Shape.Circle)
//                    .setPosition(500f, 800f)
//                    .setDelay(600)
//                    .burst(100)
            )
        )
    }

    fun ended() {
        _state.value = State.Idle
    }

    sealed class State {
        class Started(val particleSystem: List<Party>) : State()
        object Idle : State()
    }
}
