package nl.dionsegijn.konfetti

import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.support.annotation.ColorInt
import nl.dionsegijn.konfetti.models.Confetti
import nl.dionsegijn.konfetti.models.Location
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import java.util.*

/**
 * Created by dionsegijn on 3/26/17.
 */
class ParticleSystem(val renderer: KonfettiView) {

    private var location = Location()
    private var gravity = Vector(0f, 0.01f)
    private var wind = Vector(0.001f, 0f)
    private var colors = intArrayOf(Color.RED)
    private var possibleSizes = arrayOf(Size.SMALL)
    private var velocity = Vector(0f, 0f)
    private var acceleration = Vector(0f, 0f)

    private var spawnDelay = 20

    private val particles: MutableList<Confetti> = mutableListOf()

    fun addConfetti(location: Location) {
        val c = Confetti(
                location = Vector(location.x, location.y),
                size = possibleSizes[Random().nextInt(possibleSizes.size)],
                color = colors[Random().nextInt(colors.size)],
                velocity = velocity.copy(),
                acceleration = acceleration.copy())
        particles.add(c)
    }

    init {
        gravity = Vector(0f, 0.01f)
    }

    fun addColors(@ColorInt vararg colors: Int): ParticleSystem {
        this.colors = colors
        return this
    }

    fun addSizes(vararg possibleSizes: Size): ParticleSystem {
        this.possibleSizes = possibleSizes as Array<Size>
        return this
    }

    fun velocity(x: Float, y: Float): ParticleSystem {
        velocity = Vector(x, y)
        return this
    }

    fun wind(x: Float, y: Float): ParticleSystem {
        wind = Vector(x, y)
        return this
    }

    fun acceleration(x: Float, y: Float): ParticleSystem {
        acceleration = Vector(x, y)
        return this
    }

    fun fromPoint(x: Float, y: Float): ParticleSystem {
        location.setX(x)
        location.setY(y)
        return this
    }

    fun setSpawnDelay(delay: Int) {
        this.spawnDelay = delay
    }

    fun betweenPoints(x1: Float, x2: Float, y1: Float, y2: Float): ParticleSystem {
        location.betweenX(x1, x2)
        location.betweenY(y1, y2)
        return this
    }

    fun start() {
        renderer.start(this)
    }

    var handler: Handler = Handler()

    internal fun startTimer() {
        handler.postDelayed({
            addConfetti(location)
            startTimer()
        }, spawnDelay.toLong())
    }

    fun render(canvas: Canvas) {
        val it = particles.iterator()
        while (it.hasNext()) {
            val c = it.next()
            c.applyForce(gravity)
            if(c.location.y > canvas.height * 0.2) {
                c.applyForce(wind)
            }
            c.render(canvas)
            if (c.isDead()) it.remove()
        }
    }

}
