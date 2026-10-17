package nl.dionsegijn.konfetti.compose

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.image.DrawableImage
import nl.dionsegijn.konfetti.xml.image.ImageStore

@Composable
fun KonfettiView(
    modifier: Modifier = Modifier,
    parties: List<Party>,
    updateListener: OnParticleSystemUpdateListener? = null,
) {
    /**
     * Particles to draw
     */
    val particles = remember { mutableStateOf(emptyList<Particle>()) }

    /**
     * Latest stored frame time in milliseconds.
     * Cleared when the app resumes so a background pause is not applied as one frame.
     */
    val frameTime = remember { mutableStateOf(0L) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    frameTime.value = 0L
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    /**
     * Area in which the particles are being drawn
     */
    val drawArea = remember { mutableStateOf(CoreRectImpl()) }

    /**
     * Store for drawable images
     */
    val imageStore = remember { ImageStore() }

    // A running party calls the latest listener, not the one from the first composition
    val currentUpdateListener by rememberUpdatedState(updateListener)

    // The density of this composition, which follows display size and density overrides
    val pixelDensity = LocalDensity.current.density

    // Start over whenever a different list of parties comes in
    LaunchedEffect(parties) {
        // The previous run's last frame time would count as one long frame
        frameTime.value = 0L
        val partySystems =
            parties.map {
                PartySystem(
                    party = storeImages(it, imageStore),
                    pixelDensity = pixelDensity,
                )
            }.toMutableList()
        // Stop asking for frames once every system is done
        while (partySystems.isNotEmpty()) {
            withInfiniteAnimationFrameMillis { frameMs ->
                val deltaMs = frameDeltaMillis(frameTime.value, frameMs)
                frameTime.value = frameMs

                particles.value = renderFrame(partySystems, deltaMs.div(1000f), drawArea.value, currentUpdateListener)
            }
        }
        particles.value = emptyList()
    }

    Canvas(
        modifier =
            modifier
                .onGloballyPositioned {
                    drawArea.value =
                        CoreRectImpl(0f, 0f, it.size.width.toFloat(), it.size.height.toFloat())
                },
        onDraw = {
            particles.value.forEach { particle ->
                withTransform({
                    rotate(
                        degrees = particle.rotation,
                        pivot =
                            Offset(
                                x = particle.x + (particle.width / 2),
                                y = particle.y + (particle.height / 2),
                            ),
                    )
                    scale(
                        scaleX = particle.scaleX,
                        scaleY = 1f,
                        pivot = Offset(particle.x + (particle.width / 2), particle.y),
                    )
                }) {
                    particle.shape.draw(drawScope = this, particle = particle, imageStore = imageStore)
                }
            }
        },
    )
}

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

fun getTotalTimeRunning(startTime: Long): Long {
    val currentTime = System.currentTimeMillis()
    return (currentTime - startTime)
}
