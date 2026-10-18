package nl.dionsegijn.konfetti.core.emitter

import java.util.concurrent.TimeUnit
import kotlin.time.toDurationUnit

// Keeps Emitter(100, TimeUnit.MILLISECONDS) compiling for Kotlin callers on the JVM
fun Emitter(
    duration: Long,
    timeUnit: TimeUnit,
): Emitter = Emitter(duration, timeUnit.toDurationUnit())
