package nl.dionsegijn.konfetti.benchmark

import nl.dionsegijn.konfetti.core.models.CoreRectImpl

const val SEED = 42L

/** One frame at 60fps, in seconds */
const val DELTA_TIME = 1f / 60f

const val PIXEL_DENSITY = 2.75f

fun drawArea() = CoreRectImpl(0f, 0f, 1080f, 2400f)
