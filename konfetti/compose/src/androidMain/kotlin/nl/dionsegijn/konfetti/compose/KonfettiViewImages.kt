// Part of the same JVM class as KonfettiView, where these helpers lived before 3.0
@file:JvmName("KonfettiViewKt")
@file:JvmMultifileClass

package nl.dionsegijn.konfetti.compose

import android.graphics.drawable.Drawable
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.image.DrawableImage
import nl.dionsegijn.konfetti.xml.image.ImageStore

/**
 * Transforms the shapes in the given [Party] object. If a shape is a [Shape.DrawableShape],
 * it replaces the [DrawableImage] with a [ReferenceImage] and stores the [Drawable] in the [ImageStore].
 *
 * @param party The Party object containing the shapes to be transformed.
 * @return A new Party object with the transformed shapes.
 */
fun storeImages(
    party: Party,
    imageStore: ImageStore,
): Party {
    val transformedShapes =
        party.shapes.map { shape ->
            when (shape) {
                is Shape.DrawableShape -> {
                    val referenceImage = drawableToReferenceImage(shape.image as DrawableImage, imageStore)
                    shape.copy(image = referenceImage)
                }
                else -> shape
            }
        }
    return party.copy(shapes = transformedShapes)
}

/**
 * Converts a [DrawableImage] to a [ReferenceImage] and stores the [Drawable] in the [ImageStore].
 *
 * @param drawableImage The DrawableImage to be converted.
 * @return A ReferenceImage with the same dimensions as the DrawableImage and a reference to the stored Drawable.
 */
fun drawableToReferenceImage(
    drawableImage: DrawableImage,
    imageStore: ImageStore,
): ReferenceImage {
    val id = imageStore.storeImage(drawableImage.drawable)
    return ReferenceImage(id, drawableImage.width, drawableImage.height)
}
