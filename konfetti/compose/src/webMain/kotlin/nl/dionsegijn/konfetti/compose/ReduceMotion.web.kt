package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
actual fun rememberReduceMotion(): Boolean {
    val query = remember { window.matchMedia("(prefers-reduced-motion: reduce)") }
    var reduced by remember(query) { mutableStateOf(query.matches) }
    DisposableEffect(query) {
        val listener: (Event) -> Unit = { reduced = query.matches }
        query.addEventListener("change", listener)
        reduced = query.matches
        onDispose { query.removeEventListener("change", listener) }
    }
    return reduced
}
