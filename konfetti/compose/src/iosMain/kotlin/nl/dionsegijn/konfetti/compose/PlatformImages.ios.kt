package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.graphics.drawscope.DrawScope
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.CoreImageStore
import nl.dionsegijn.konfetti.core.models.Shape

// Only Android has images that need a store
internal actual fun createImageStore(): CoreImageStore<*>? = null

internal actual fun storePlatformImages(
    party: Party,
    imageStore: CoreImageStore<*>?,
): Party = party

internal actual fun DrawScope.drawPlatformImage(
    shape: Shape.DrawableShape,
    particle: Particle,
    imageStore: CoreImageStore<*>?,
) = Unit
