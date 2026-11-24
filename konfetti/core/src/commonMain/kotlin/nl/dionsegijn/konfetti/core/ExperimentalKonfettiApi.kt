package nl.dionsegijn.konfetti.core

/**
 * Marks Konfetti APIs that may still change before stabilizing in a future release.
 */
@RequiresOptIn(
    message = "This Konfetti API is experimental and may change in future releases.",
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class ExperimentalKonfettiApi
