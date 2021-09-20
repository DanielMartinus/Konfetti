package nl.dionsegijn.demo_compose

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.dionsegijn.konfetti_core.ParticleSystem
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size

class KonfettiViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun start(drawable: Drawable) {
        _state.value = State.Started(
            listOf(
                ParticleSystem()
                    .setDirection(0.0, 359.0)
                    .addColors(0xb48def)
                    .setSpeed(1f, 5f)
                    .addSizes(Size(8), Size(20), Size(2))
                    .setFadeOutEnabled(true)
                    .setTimeToLive(10000L)
                    .addShapes(Shape.Circle, Shape.Rectangle(0.2f), Shape.DrawableShape(drawable))
                    .setPosition(200f, 400f)
                    .setDelay(300)
                    .streamMaxParticles(10, 40),
                ParticleSystem()
                    .setDirection(0.0, 359.0)
                    .addColors(0xfce18a, 0xff726d)
                    .addSizes(Size(8), Size(20), Size(2))
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(10000L)
                    .addShapes(Shape.Rectangle(0.2f), Shape.Square)
                    .setPosition(800f, 400f)
                    .setDelay(800)
                    .streamMaxParticles(10, 40),
                ParticleSystem()
                    .setDirection(0.0, 359.0)
                    .addColors(0xf4306d)
                    .addSizes(Size(8), Size(20), Size(2))
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(10000L)
                    .addShapes(Shape.Circle)
                    .setPosition(500f, 800f)
                    .setDelay(1200)
                    .streamMaxParticles(10, 40)
            )
        )
    }

    fun ended() {
        _state.value = State.Idle
    }

    sealed class State {
        class Started(val particleSystem: List<ParticleSystem>) : State()
        object Idle : State()
    }
}
