package nl.dionsegijn.samples.multiplatform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

// Fills the playground section of index.html, the rest of the page stays plain HTML
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "playground") {
        App()
    }
}
