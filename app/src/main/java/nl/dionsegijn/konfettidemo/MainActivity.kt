package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewTreeObserver
import android.widget.SeekBar
import android.widget.TextView
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity() {

    var timer: Int = 20
    var wind: Float = 0f

    lateinit var konfetti: KonfettiView
    lateinit var seekbarX: SeekBar
    lateinit var seekbarY: SeekBar

    lateinit var fpsView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fpsView = findViewById(R.id.fps) as TextView

        konfetti = findViewById(R.id.konfetti) as KonfettiView
        konfetti.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                konfetti.viewTreeObserver.removeOnPreDrawListener(this)
                startConfetti()
                return false
            }
        })

        seekbarX = findViewById(R.id.velocityX) as SeekBar
        seekbarX.progress = timer
        seekbarX.setOnSeekBarChangeListener(getTimerSeekBarChangeListener())

        seekbarY = findViewById(R.id.velocityY) as SeekBar
        seekbarY.progress = 0
        seekbarY.setOnSeekBarChangeListener(getWindSeekBarChangeListener())

        monitorFps()
        velocityTest()
    }

    var velocityTracker: VelocityTracker = VelocityTracker.obtain()
    var startX: Float = 0f
    var startY: Float = 0f


    fun velocityTest() {
        konfetti.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    velocityTracker.clear()
                    velocityTracker.addMovement(event)
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    velocityTracker.addMovement(event)
                    velocityTracker.computeCurrentVelocity(1000)
                    Log.e("sdsa", String.format("x: %s y: %s", velocityTracker.xVelocity, velocityTracker.yVelocity))
                }
                MotionEvent.ACTION_CANCEL -> {
                    velocityTracker.recycle()
                }
            }
            true
        }
    }

    var fpsHandler: Handler = Handler()
    fun monitorFps() {
        fpsHandler.postDelayed({
            fpsView.text = String.format("%sfps", konfetti.fps.toString())
            monitorFps()
        }, 100)
    }

    fun startConfetti() {
        val colors = intArrayOf(color(R.color.confetti1), color(R.color.confetti2), color(R.color.confetti3), color(R.color.confetti4))

        konfetti.build()
                .betweenPoints(100f, konfetti.width - 200f, 200f, 200f)
                .addColors(*colors)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size.SMALL)
                .start()
    }

    fun color(resId: Int): Int {
        return ContextCompat.getColor(applicationContext, resId)
    }

    fun getTimerSeekBarChangeListener(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (konfetti.systems.isNotEmpty()) {
                    konfetti.systems.forEach {
                        timer = seekbarX.progress
                        if (fromUser) {
                            it.setSpawnDelay(timer)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }

    fun getWindSeekBarChangeListener(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (konfetti.systems.isNotEmpty()) {
                    konfetti.systems.forEach {
                        if (fromUser) {
                            it.setAngle(progress.toDouble())
                            it.setSpeed(10)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }
}
