package nl.dionsegijn.konfetti.compose

import kotlin.math.min

/**
 * Longest gap, in milliseconds, that one frame is allowed to apply.
 * A pause in the background can last many seconds. Replaying that whole gap
 * would create every particle for it in a single frame.
 */
internal const val MAX_FRAME_DELTA_MILLIS = 100L

/**
 * Time since the previous frame, in milliseconds.
 *
 * A previous time of 0 means there is no frame to measure from. That is the
 * first frame, and also the frame after the app resumes and the clock is cleared.
 * Longer gaps are limited to [MAX_FRAME_DELTA_MILLIS].
 */
internal fun frameDeltaMillis(
    previousFrameMillis: Long,
    frameMillis: Long,
): Long {
    if (previousFrameMillis <= 0L) return 0L
    val delta = frameMillis - previousFrameMillis
    if (delta <= 0L) return 0L
    return min(delta, MAX_FRAME_DELTA_MILLIS)
}
