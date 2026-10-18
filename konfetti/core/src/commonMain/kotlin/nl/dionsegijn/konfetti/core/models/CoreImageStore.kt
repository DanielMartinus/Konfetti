package nl.dionsegijn.konfetti.core.models

interface CoreImageStore<T> {
    fun storeImage(image: T): Int

    fun getImage(id: Int): T?
}
