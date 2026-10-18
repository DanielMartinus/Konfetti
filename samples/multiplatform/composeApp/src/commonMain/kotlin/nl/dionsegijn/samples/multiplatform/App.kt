package nl.dionsegijn.samples.multiplatform

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import nl.dionsegijn.konfetti.compose.imageShape
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
        Playground(
            presets =
                listOf(
                    Preset("Festive", Presets.festive(heart)),
                    Preset("Explode", Presets.explode()),
                    parade,
                    Preset("Rain", Presets.rain()),
                    Preset("Shapes", Presets.shapes()),
                ),
            initial = parade,
        )
    }
}
