package nl.dionsegijn.konfetti.core.models

import java.nio.ByteBuffer

interface CoreImage {
    val width: Int
    val height: Int
}

data class ReferenceImage(
    val reference: Int,
    override val width: Int,
    override val height: Int
): CoreImage