// Keeps the JVM class name from before 3.0, so existing callers keep working
@file:JvmName("ReduceMotionKt")

package nl.dionsegijn.konfetti.compose

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    var reduced by remember(context) { mutableStateOf(isReduceMotionEnabled(context)) }
    DisposableEffect(context) {
        val resolver = context.contentResolver
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    reduced = isReduceMotionEnabled(context)
                }
            }
        resolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            observer,
        )
        reduced = isReduceMotionEnabled(context)
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return reduced
}

// "Remove animations" sets the animation scale to 0
internal fun isReduceMotionEnabled(context: Context): Boolean =
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
