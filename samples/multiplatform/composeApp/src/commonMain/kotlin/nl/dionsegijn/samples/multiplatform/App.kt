package nl.dionsegijn.samples.multiplatform

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import nl.dionsegijn.konfetti.compose.imageShape
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.json.KonfettiJson
import nl.dionsegijn.samples.multiplatform.resources.Res
import nl.dionsegijn.samples.multiplatform.resources.heart
import nl.dionsegijn.samples.playground.Playground
import nl.dionsegijn.samples.playground.Preset
import org.jetbrains.compose.resources.painterResource

// The multiplatform demo is the playground: pick a preset, scrub it on the timeline and tweak it live
@Composable
fun App() {
    MaterialTheme {
        val heart = imageShape(painterResource(Res.drawable.heart))
        val parade = Preset("Parade", Presets.parade())
        val studio = studioExport()
        Playground(
            presets =
                listOfNotNull(
                    Preset("Festive", Presets.festive(heart)),
                    Preset("Explode", Presets.explode()),
                    parade,
                    Preset("Rain", Presets.rain()),
                    Preset("Shapes", Presets.shapes()),
                    studio?.let { Preset("Studio", it) },
                ),
            initial = parade,
        )
    }
}

// A file exported from Konfetti Studio, to check a device shows the same confetti as Studio
@Composable
private fun studioExport(): List<Party>? {
    val density = LocalDensity.current.density
    var parties by remember { mutableStateOf<List<Party>?>(null) }
    LaunchedEffect(density) {
        val json = Res.readBytes("files/studio-handoff.konfetti.json").decodeToString()
        parties = KonfettiJson.decode(json, density)
    }
    return parties
}
