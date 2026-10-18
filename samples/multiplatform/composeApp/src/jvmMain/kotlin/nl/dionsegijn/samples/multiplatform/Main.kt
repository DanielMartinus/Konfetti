package nl.dionsegijn.samples.multiplatform

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

// Run with ./gradlew :samples:multiplatform:composeApp:run
fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Konfetti",
            state = rememberWindowState(width = 420.dp, height = 760.dp),
        ) {
            App()
        }
    }
