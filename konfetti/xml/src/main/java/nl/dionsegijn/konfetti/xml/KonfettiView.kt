package nl.dionsegijn.konfetti.xml

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import nl.dionsegijn.konfetti.core.Particle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.models.CoreRectImpl
import nl.dionsegijn.konfetti.core.models.ReferenceImage
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.image.DrawableImage
import nl.dionsegijn.konfetti.xml.image.ImageStore
import nl.dionsegijn.konfetti.xml.listeners.OnParticleSystemUpdateListener
import kotlin.math.min

/**
 * Longest gap, in seconds, that one frame is allowed to apply. The same as the Compose view.
 * An app paused while this view stays visible would otherwise replay the whole pause in one frame.
 */
private const val MAX_FRAME_DELTA_SECONDS = 0.1f

/**
 * Implement this view to render the particles on.
 * Call [build] to setup a particle system. KonfettiView will then invalidate
 * pass the canvas to each system where each system will handle the rendering.
 */
open class KonfettiView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Active particle systems
     */
    private val systems: MutableList<PartySystem> = mutableListOf()

    /**
     * Keeping track of the delta time between frame rendering.
     * Nullable because API 21 and 22 call [onVisibilityChanged] from the [View]
     * constructor, before this initializer has run.
     */
    private var timer: TimerIntegration? = TimerIntegration()

    private var drawArea = CoreRectImpl()

    /**
     * [OnParticleSystemUpdateListener] listener to notify when a new particle system
     * starts rendering and when a particle system stopped rendering
     */
    var onParticleSystemUpdateListener: OnParticleSystemUpdateListener? = null

    fun getActiveSystems() = systems

    private val imageStore = ImageStore()

    /**
     * Check if current systems are active rendering particles.
     * @return true if konfetti is actively rendering
     *         false if everything stopped rendering
     */
    fun isActive() = systems.isNotEmpty()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val frameTimer = timer ?: return
        val deltaTime = frameTimer.getDeltaTime()
        for (i in systems.size - 1 downTo 0) {
            val partySystem = systems[i]

            val totalTimeRunning = frameTimer.getTotalTimeRunning(partySystem.createdAt)
            if (totalTimeRunning >= partySystem.party.delay) {
                partySystem.render(deltaTime, drawArea).forEach {
                    it.display(canvas)
                }
            }

            if (partySystem.isDoneEmitting()) {
                systems.removeAt(i)
                onParticleSystemUpdateListener?.onParticleSystemEnded(this, partySystem.party, systems.size)
            }
        }

        if (systems.size != 0) {
            invalidate()
        } else {
            frameTimer.reset()
        }
    }

    private val paint: Paint = Paint()

    private fun Particle.display(canvas: Canvas) {
        // setting alpha via paint.setAlpha allocates a temporary "ColorSpace$Named" object
        // it is more efficient via setColor
        paint.color = color

        val centerX = scaleX * width / 2

        val saveCount = canvas.save()
        canvas.translate(x - centerX, y)
        canvas.rotate(rotation, centerX, width / 2)
        canvas.scale(scaleX, 1f)

        shape.draw(canvas, paint, width, imageStore)
        canvas.restoreToCount(saveCount)
    }

    fun start(vararg party: Party) {
        systems.addAll(
            party.map {
                onParticleSystemUpdateListener?.onParticleSystemStarted(this, it, systems.size)
                PartySystem(party = storeImages(it), pixelDensity = Resources.getSystem().displayMetrics.density)
            },
        )
        invalidate()
    }

    fun start(party: List<Party>) {
        systems.addAll(
            party.map {
                onParticleSystemUpdateListener?.onParticleSystemStarted(this, it, systems.size)
                PartySystem(party = storeImages(it), pixelDensity = Resources.getSystem().displayMetrics.density)
            },
        )
        invalidate()
    }

    fun start(party: Party) {
        onParticleSystemUpdateListener?.onParticleSystemStarted(this, party, systems.size)
        systems.add(PartySystem(party = storeImages(party), pixelDensity = Resources.getSystem().displayMetrics.density))
        invalidate()
    }

    /**
     * Transforms the shapes in the given [Party] object. If a shape is a [Shape.DrawableShape],
     * it replaces the [DrawableImage] with a [ReferenceImage] and stores the [Drawable] in the [ImageStore].
     *
     * @param party The Party object containing the shapes to be transformed.
     * @return A new Party object with the transformed shapes.
     */
    private fun storeImages(party: Party): Party {
        val transformedShapes =
            party.shapes.map { shape ->
                when (shape) {
                    is Shape.DrawableShape -> {
                        val referenceImage = drawableToReferenceImage(shape.image as DrawableImage)
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
    fun drawableToReferenceImage(drawableImage: DrawableImage): ReferenceImage {
        val id = imageStore.storeImage(drawableImage.drawable)
        return ReferenceImage(id, drawableImage.width, drawableImage.height)
    }

    /**
     * Stop a particular particle system. All particles belonging to this system will directly disappear from the view.
     */
    fun stop(party: Party) {
        systems.removeAll { it.party == party }
        onParticleSystemUpdateListener?.onParticleSystemEnded(this, party, systems.size)
    }

    /**
     * Abruptly stop all particle systems from rendering
     * The canvas will stop drawing all particles. Everything that's being rendered will directly
     * disappear from the view.
     */
    fun reset() {
        systems.clear()
    }

    /**
     * Stop the particle systems from rendering new particles. All particles already visible
     * will continue rendering until they're done. When all particles are done rendering the system
     * will be removed.
     */
    fun stopGracefully() {
        systems.forEach { it.enabled = false }
    }

    /**
     * TimerIntegration retrieves the delta time since the rendering of the previous frame.
     * Delta time is used to draw the confetti correctly if any frame drops occur.
     * One frame never applies more than [MAX_FRAME_DELTA_SECONDS].
     */
    class TimerIntegration {
        private var previousTime: Long = -1L

        fun reset() {
            previousTime = -1L
        }

        fun getDeltaTime(): Float {
            if (previousTime == -1L) previousTime = System.nanoTime()

            val currentTime = System.nanoTime()
            val dt = (currentTime - previousTime) / 1000000f
            previousTime = currentTime
            return min(dt / 1000, MAX_FRAME_DELTA_SECONDS)
        }

        fun getTotalTimeRunning(startTime: Long): Long {
            val currentTime = System.currentTimeMillis()
            return (currentTime - startTime)
        }
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int,
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawArea = CoreRectImpl(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onVisibilityChanged(
        changedView: View,
        visibility: Int,
    ) {
        super.onVisibilityChanged(changedView, visibility)
        timer?.reset()
    }

    companion object {
        /**
         * True when the user turned on "Remove animations" in the accessibility settings.
         * Check it before starting a party, for example to skip the confetti. Always false below API 17.
         */
        @JvmStatic
        fun isReduceMotionEnabled(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return false
            // "Remove animations" sets the animation scale to 0
            return Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }
    }
}
