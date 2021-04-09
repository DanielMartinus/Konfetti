package nl.dionsegijn.konfetti_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameMillis

@Composable
fun startTimerIntegration(): State<List<Particle>> {
    val millisState = mutableStateOf(listOf(Particle(200f, 200f, 25f, 25f)))
    LaunchedEffect(true) {
        val startTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameTime ->
//                val old = millisState.value
//                val newValue = frameTime - startTime
//                millisState.value = newValue
//                Log.e("DION", "DION + ${newValue - old}")
                millisState.value = millisState.value.map { update(it) }
            }
        }
    }
    return millisState
}

fun update(particle: Particle): Particle {
    val newWidth = particle.width - 2

    return particle.copy(width = if(newWidth < -100) 100f else newWidth)
}

data class Particle(val x: Float, val y: Float, val width: Float, val height: Float)
