package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSectionButtons()
    }

    fun setupSectionButtons() {
        buttonSectionBurstFromTop.setOnClickListener {
            // TODO implement loading config for this section
        }
        buttonSectionDragAndBurst.setOnClickListener {
            // TODO implement loading config for this section
        }
        buttonSectionStreamFromCenter.setOnClickListener {
            // TODO implement loading config for this section
        }
    }

}
