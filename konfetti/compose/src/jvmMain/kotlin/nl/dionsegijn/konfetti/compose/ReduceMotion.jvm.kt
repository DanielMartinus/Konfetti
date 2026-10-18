// Same JVM class name as on Android
@file:JvmName("ReduceMotionKt")

package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable

// Desktop has no reduce motion setting that works the same on every OS
@Composable
actual fun rememberReduceMotion(): Boolean = false
