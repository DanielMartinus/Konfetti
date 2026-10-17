package nl.dionsegijn.konfetti.xml.image

import android.graphics.drawable.Drawable
import nl.dionsegijn.konfetti.core.models.CoreImageStore

/**
 * The ImageStore class is used to store Drawable objects and provide a way to reference them.
 * This is done for performance reasons and to allow the core library, which can't use Android Drawables,
 * to work with images.
 *
 * Instead of converting a Drawable to a ByteBuffer, then to a Bitmap, and then back to a Drawable,
 * which is inefficient for the render code, the Drawable is stored in the ImageStore.
 * The rest of the application can then work with a simple integer reference to the Drawable.
 *
 * The ImageStore provides methods to store a Drawable and retrieve it using its reference.
 */
class ImageStore : CoreImageStore<Drawable> {
    private val images = mutableMapOf<Int, Drawable>()

    override fun storeImage(image: Drawable): Int {
        val id = image.hashCode()
        images[id] = image
        return id
    }

    override fun getImage(id: Int): Drawable? {
        return images[id]
    }
}
