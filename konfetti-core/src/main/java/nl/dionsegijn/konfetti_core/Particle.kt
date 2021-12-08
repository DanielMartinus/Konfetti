package nl.dionsegijn.konfetti_core

import nl.dionsegijn.konfetti_core.models.Shape

/**
 * Particle holding exact data to instruct where and how to draw a particle
 */
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
