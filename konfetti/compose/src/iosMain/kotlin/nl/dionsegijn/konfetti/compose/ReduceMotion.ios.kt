package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityReduceMotionStatusDidChangeNotification

@Composable
actual fun rememberReduceMotion(): Boolean {
    var reduced by remember { mutableStateOf(UIAccessibilityIsReduceMotionEnabled()) }
    DisposableEffect(Unit) {
        val observer =
            NSNotificationCenter.defaultCenter.addObserverForName(
                name = UIAccessibilityReduceMotionStatusDidChangeNotification,
                `object` = null,
                queue = NSOperationQueue.mainQueue,
            ) { _ ->
                reduced = UIAccessibilityIsReduceMotionEnabled()
            }
        reduced = UIAccessibilityIsReduceMotionEnabled()
        onDispose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }
    return reduced
}
