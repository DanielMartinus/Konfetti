package nl.dionsegijn.xml.kotlin

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.samples.shared.Presets
import java.util.concurrent.TimeUnit

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
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(Presets.festive())
    }

    private fun explode() {
        /**
         * See [Presets] for this configuration
         */
        viewKonfetti.start(listOf(
            Party(
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(0.5, 0.3)
            )
        ))
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
