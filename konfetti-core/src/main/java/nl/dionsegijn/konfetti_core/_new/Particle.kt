package nl.dionsegijn.konfetti_core._new

import nl.dionsegijn.konfetti_core.models.Shape

// Particle ready to render
data class Particle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int,
    val rotation: Float,
    val scaleX: Float,
    val shape: Shape,
    val alpha: Int
)
