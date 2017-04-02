package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
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

    fun startConfetti() {
        val colors = intArrayOf(color(R.color.confetti1), color(R.color.confetti2), color(R.color.confetti3), color(R.color.confetti4))

        konfetti.build()
//                .betweenPoints((konfetti.width / 2f) - 80, (konfetti.width / 2f) + 80, 250f, 250f)
                .addColors(*colors)
                .setDirection(240.0, 300.0)
                .setSpeed(3f, 6f)
//                .setAngle(0.0, 360.0)
//                .setSpeed(5)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size.SMALL)
//                .emit(5000, 1000)
    }

    var startX: Float = 0f
    var startY: Float = 0f
    var speed: Int = 0
    var degrees: Double = 0.0
    fun velocityTest() {
        konfetti.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - startX
                    val dy = event.y - startY
                    val r = Math.atan2(dy.toDouble(), dx.toDouble()) // In radians
                    degrees = ((r * (180 / Math.PI) - 180) + 360) % 360

                    val length = Math.sqrt((dx * dx) + (dy * dy).toDouble())
                    speed = (length / 100).toInt()
                    if(speed > 10) speed = 0
                }
                MotionEvent.ACTION_UP -> {
                    val colors = intArrayOf(color(R.color.confetti1), color(R.color.confetti2), color(R.color.confetti3), color(R.color.confetti4))
                    konfetti.build()
                            .addColors(*colors)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(Size.SMALL, Size.MEDIUM)
                            .setPosition(-50f, konfetti.width + 50f, -50f, -50f)
                            .emit(100, 0, 200)
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
//                            it.setSpawnDelay(timer)
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
//                            it.setAngle(progress.toDouble())
//                            it.setSpeed(10)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }
}
