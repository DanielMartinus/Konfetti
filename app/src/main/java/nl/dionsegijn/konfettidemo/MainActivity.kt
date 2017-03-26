package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.ViewTreeObserver
import android.widget.SeekBar
import nl.dionsegijn.konfetti.KonfettiView

/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity() {

    var velocityX: Float = 0f
    var wind: Float = 0f

    lateinit var konfetti: KonfettiView
    lateinit var seekbarX: SeekBar
    lateinit var seekbarY: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        konfetti = findViewById(R.id.konfetti) as KonfettiView
        konfetti.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                konfetti.viewTreeObserver.removeOnPreDrawListener(this)
                startConfetti()
                return false
            }
        })

        seekbarX = findViewById(R.id.velocityX) as SeekBar
        seekbarX.progress = ((velocityX * 10) - 25).toInt()
        seekbarX.setOnSeekBarChangeListener(getOnVelocitySeekBarChangeListener())

        seekbarY = findViewById(R.id.velocityY) as SeekBar
        seekbarY.progress = wind.toInt()
        seekbarY.setOnSeekBarChangeListener(getWindSeekBarChangeListener())
    }

    fun startConfetti() {
        konfetti.build()
                .betweenPoints(-50f, konfetti.width.toFloat() - 50, -40f, -40f)
                .addColors(color(R.color.confetti1), color(R.color.confetti2), color(R.color.confetti3), color(R.color.confetti4))
                .start()
    }

    fun color(resId: Int) : Int {
        return ContextCompat.getColor(applicationContext, resId)
    }

    fun getOnVelocitySeekBarChangeListener(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (konfetti.konfettiSystems.isNotEmpty()) {
                    konfetti.konfettiSystems.forEach {
                        velocityX = (seekbarX.progress - 25) / 10f
                        if (fromUser) {
                            it.velocity(velocityX, 0f)
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
                if (konfetti.konfettiSystems.isNotEmpty()) {
                    konfetti.konfettiSystems.forEach {
                        wind = seekbarY.progress.toFloat() / 100f
                        if (fromUser) {
                            it.wind(wind, 0f)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }
}
