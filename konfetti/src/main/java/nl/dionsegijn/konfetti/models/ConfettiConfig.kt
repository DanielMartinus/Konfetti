package nl.dionsegijn.konfetti.models

/**
 * ConfettiConfig is a simple data class to set some
 * specific configurations for confetti upon creation
 */
data class ConfettiConfig(
    var fadeOut: Boolean = false,
    var timeToLive: Long = 2000L,
    var rotate: Boolean = true,
    var accelerate: Boolean = true,
    var delay: Long = 0L,
    var speedDensityIndependent: Boolean = true
)
