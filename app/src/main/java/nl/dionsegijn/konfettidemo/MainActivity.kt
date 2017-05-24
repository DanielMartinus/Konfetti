package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfettidemo.interfaces.SimpleOnTabSelectedListener

/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity() {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupTabSelectionBottomSheetBehavior()
        bottomSheetBehavior = BottomSheetBehavior.from(viewConfigurationControls)

        viewKonfetti.setOnClickListener {
            startConfetti()
        }
    }

    /**
     * Implement expand and collapse behavior for the BottomSheet used to display the configuration
     * options.
     * - Reselect a tab and the bottom sheet will either collapse or expand depending on its current
     * state.
     * - Select a tab that wasn't active yet and the BottomSheet will expand
     */
    fun setupTabSelectionBottomSheetBehavior() {
        viewConfigurationControls.setOnTabSelectedListener(object : SimpleOnTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state =
                        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            BottomSheetBehavior.STATE_EXPANDED
                        } else {
                            BottomSheetBehavior.STATE_COLLAPSED
                        }
            }
        })
    }

    fun startConfetti() {
        val colors = intArrayOf(color(R.color.lt_yellow), color(R.color.lt_orange), color(R.color.lt_purple), color(R.color.lt_pink))
        val config = viewConfigurationControls.configuration.active
        viewKonfetti.build()
                .addColors(*colors)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(config.timeToLive)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size.SMALL)
                .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f) // TODO doesn't change, always the
                .stream(300, 5000L)
    }

    fun color(resId: Int): Int {
        return ContextCompat.getColor(applicationContext, resId)
    }


}
