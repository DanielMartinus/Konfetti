package nl.dionsegijn.samples.playground

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.dionsegijn.samples.playground.resources.Res
import nl.dionsegijn.samples.playground.resources.jetbrains_mono_regular
import nl.dionsegijn.samples.playground.resources.jetbrains_mono_semibold
import org.jetbrains.compose.resources.Font

// Same palette as the konfetti.dev mockup: white paper, ink, and the confetti colors as accents
internal object Palette {
    val paper = Color(0xFFFFFFFF)
    val soft = Color(0xFFF6F5F9)
    val line = Color(0xFFE9E7EF)
    val ink = Color(0xFF16151D)
    val muted = Color(0xFF6C6A7A)
    val accent = Color(0xFFF4306D)
    val dot = Color(0xFFE4E2EA)
    val confetti = listOf(Color(0xFFFCE18A), Color(0xFFFF726D), Color(0xFFF4306D), Color(0xFFB48DEF))
}

// Bundled, because the web build has no monospace font to fall back to
internal val Mono: FontFamily
    @Composable get() =
        FontFamily(
            Font(Res.font.jetbrains_mono_regular, FontWeight.Normal),
            Font(Res.font.jetbrains_mono_semibold, FontWeight.SemiBold),
        )

/** Four little pieces of confetti, the Konfetti logo */
@Composable
internal fun LogoMark(modifier: Modifier = Modifier) {
    Canvas(modifier.size(22.dp)) {
        val unit = size.width / 22f
        rotate(12f, pivot = Offset(4.5f * unit, 4.5f * unit)) {
            drawRoundRect(Palette.confetti[2], Offset.Zero, Size(9 * unit, 9 * unit), CornerRadius(2 * unit))
        }
        drawCircle(Palette.confetti[3], radius = 4.5f * unit, center = Offset(17.5f * unit, 6.5f * unit))
        rotate(-20f, pivot = Offset(7.5f * unit, 17.5f * unit)) {
            drawRoundRect(Palette.confetti[0], Offset(3 * unit, 13 * unit), Size(9 * unit, 9 * unit), CornerRadius(2 * unit))
        }
        rotate(30f, pivot = Offset(18 * unit, 15.5f * unit)) {
            drawRoundRect(Palette.confetti[1], Offset(15 * unit, 10 * unit), Size(6 * unit, 11 * unit), CornerRadius(2 * unit))
        }
    }
}
