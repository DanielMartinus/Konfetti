package nl.dionsegijn.konfetti.compose

import androidx.compose.ui.graphics.drawscope.DrawScope
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.CoreImageStore
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.image.ImageStore

internal actual fun createImageStore(): CoreImageStore<*>? = ImageStore()

internal actual fun storePlatformImages(
    party: Party,
    imageStore: CoreImageStore<*>?,
): Party = storeImages(party, imageStore as ImageStore)

internal actual fun DrawScope.drawPlatformImage(
    shape: Shape.DrawableShape,
    particle: Particle,
    imageStore: CoreImageStore<*>?,
) = shape.drawDrawable(this, particle, imageStore as ImageStore)
