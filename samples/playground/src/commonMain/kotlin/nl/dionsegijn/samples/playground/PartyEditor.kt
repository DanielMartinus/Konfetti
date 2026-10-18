package nl.dionsegijn.samples.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.Size
import kotlin.math.roundToInt

/** Sliders for the selected lane. Every change shows on the canvas right away. */
@Composable
internal fun PartyEditor(
    party: Party,
    onChange: (Party) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LabeledSlider("Angle", "${party.angle}°", party.angle.toFloat(), 0f..359f) {
            onChange(party.copy(angle = it.roundToInt()))
        }
        LabeledSlider("Spread", "${party.spread}°", party.spread.toFloat(), 0f..360f) {
            onChange(party.copy(spread = it.roundToInt()))
        }
        LabeledSlider("Max speed", "${party.maxSpeed.roundToInt()}", party.maxSpeed, 0f..80f) {
            onChange(party.copy(maxSpeed = it.roundToInt().toFloat()))
        }
        if (party.emitter.emittingTime > 0L) {
            LabeledSlider("Particles", "${party.particleCount()}", party.particleCount().toFloat(), 1f..300f) {
                onChange(party.withParticleCount(it.roundToInt()))
            }
        }
        val sizeDp = party.size.first().sizeInDp
        LabeledSlider("Size", "$sizeDp dp", sizeDp.toFloat(), 4f..24f) {
            onChange(party.copy(size = listOf(Size(it.roundToInt()))))
        }
        LabeledSlider("Delay", "${party.delay} ms", party.delay.toFloat(), 0f..3000f) {
            // Snap to 50 ms, so lanes line up easily
            onChange(party.copy(delay = (it / 50f).roundToInt() * 50))
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: String,
    position: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Palette.ink)
            Text(value, fontSize = 12.sp, fontFamily = Mono, color = Palette.muted)
        }
        Slider(
            value = position.coerceIn(range),
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.height(28.dp),
            colors =
                SliderDefaults.colors(
                    thumbColor = Palette.ink,
                    activeTrackColor = Palette.ink,
                    inactiveTrackColor = Palette.line,
                ),
        )
    }
}
