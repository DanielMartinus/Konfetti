package nl.dionsegijn.xml.kotlin

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.xml.image.ImageUtil
import nl.dionsegijn.samples.shared.Presets

class MainActivity : AppCompatActivity() {
    private lateinit var viewKonfetti: KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewKonfetti = findViewById(R.id.konfettiView)
        findViewById<Button>(R.id.btnFestive).setOnClickListener { festive() }
        findViewById<Button>(R.id.btnExplode).setOnClickListener { explode() }
        findViewById<Button>(R.id.btnParade).setOnClickListener { parade() }
        findViewById<Button>(R.id.btnRain).setOnClickListener { rain() }
    }

    private fun festive() {
        val drawable = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_heart)
        val drawableShape = ImageUtil.loadDrawable(drawable!!)

        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.festive(drawableShape))
    }

    private fun explode() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.explode())
    }

    private fun parade() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.parade())
    }

    private fun rain() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.rain())
    }
}
