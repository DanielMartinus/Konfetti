package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

@Composable
actual fun rememberReduceMotion(): Boolean = remember { window.matchMedia("(prefers-reduced-motion: reduce)").matches }
