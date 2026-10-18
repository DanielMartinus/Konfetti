package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.graphics.drawscope.DrawScope
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.CoreImageStore
import nl.dionsegijn.konfetti.core.models.Shape

// Core can't hold platform images like Android drawables. Android keeps them in an image store
// and hands core a reference instead, the other platforms have nothing to store.

/** Store for the platform's images, or null when the platform has none */
internal expect fun createImageStore(): CoreImageStore<*>?

/** Swap the platform images in [party] for references into [imageStore] */
internal expect fun storePlatformImages(
    party: Party,
    imageStore: CoreImageStore<*>?,
): Party

/** Draw an image shape that [storePlatformImages] stored */
internal expect fun DrawScope.drawPlatformImage(
    shape: Shape.DrawableShape,
    particle: Particle,
    imageStore: CoreImageStore<*>?,
)
