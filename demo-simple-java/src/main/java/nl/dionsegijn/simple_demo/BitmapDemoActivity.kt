package nl.dionsegijn.simple_demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class BitmapDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)

        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
        konfettiView.setOnClickListener {

            val bat = Shape.BITMAP(getBitmap(R.drawable.bat),
                listOf(Color.parseColor("#272121"),
                    Color.parseColor("#443737"),
                    Color.parseColor("#ff4d00"),
                    Color.parseColor("#ff0000")),
                0.035f,
                0.12f
            )
            val ghost = Shape.BITMAP(getBitmap(R.drawable.ghost),
                listOf(Color.parseColor("#272121"),
                    Color.parseColor("#443737"),
                    Color.parseColor("#ff4d00"),
                    Color.parseColor("#ff0000")),
                0.035f,
                0.12f
            )
            val skull = Shape.BITMAP(getBitmap(R.drawable.skull),
                listOf(Color.parseColor("#272121"),
                    Color.parseColor("#443737"),
                    Color.parseColor("#ff4d00"),
                    Color.parseColor("#ff0000")),
                0.035f,
                0.12f
            )
            val spider = Shape.BITMAP(getBitmap(R.drawable.spider),
                listOf(Color.parseColor("#272121"),
                    Color.parseColor("#443737"),
                    Color.parseColor("#ff4d00"),
                    Color.parseColor("#ff0000")),
                0.035f,
                0.12f
            )
            val web = Shape.BITMAP(getBitmap(R.drawable.web),
                listOf(Color.parseColor("#272121"),
                    Color.parseColor("#443737"),
                    Color.parseColor("#ff4d00"),
                    Color.parseColor("#ff0000")),
                0.035f,
                0.12f
            )

            konfettiView.build()
                //.addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(8f, 15f)
                .setFadeOutEnabled(true)
                .setTimeToLive(3000L) //Shape.CIRCLE, Shape.RECT,
                .addShapes(bat, ghost, skull, spider, web)
                .addSizes(Size(22, 3f))
                .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                .streamFor(200, 5000L)

        }
    }

    private fun getBitmap(resId: Int) : Bitmap {
        return BitmapFactory.decodeResource(resources,resId)
    }
}
