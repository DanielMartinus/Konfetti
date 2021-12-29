package nl.dionsegijn.xml.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.samples.shared.Presets

class KonfettiViewModel : ViewModel() {

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    fun festive() {
        /**
         * See [Presets] for this configuration
         */
        _state.value = State.Started(Presets.festive())
    }

    fun explode() {
        /**
         * See [Presets] for this configuration
         */
        _state.value = State.Started(Presets.explode())
    }

    fun parade() {
        /**
         * See [Presets] for this configuration
         */
        _state.value = State.Started(Presets.parade())
    }

    fun rain() {
        /**
         * See [Presets] for this configuration
         */
        _state.value = State.Started(Presets.rain())
    }

    fun ended() {
        _state.value = State.Idle
    }

    sealed class State {
        class Started(val party: List<Party>) : State()
        object Idle : State()
    }
}
