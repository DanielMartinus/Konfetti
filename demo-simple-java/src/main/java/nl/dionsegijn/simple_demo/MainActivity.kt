package nl.dionsegijn.simple_demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
        konfettiView.setOnClickListener {
            konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size(12, 5f))
                .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                .streamFor(300, 5000L)
        }

        findViewById<View>(R.id.demo_bitmap).setOnClickListener {
            startActivity(Intent(this@MainActivity, BitmapDemoActivity::class.java))
        }
    }
}
