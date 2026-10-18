package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable

/**
 * True when the user asked the system to reduce motion, like "Remove animations" on Android
 * or "Reduce Motion" on iOS. Check it before starting a party, for example to skip the confetti.
 */
@Composable
expect fun rememberReduceMotion(): Boolean
