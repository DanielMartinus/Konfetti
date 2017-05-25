package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfettidemo.interfaces.OnSimpleTabSelectedListener

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
        viewConfigurationControls.setOnTabSelectedListener(object : OnSimpleTabSelectedListener() {
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
        val config = viewConfigurationControls.configuration.active
        val selectedColors = config.colors.map { color(it) }.toIntArray()
        viewKonfetti.build()
                .addColors(*selectedColors)
                .setDirection(0.0, 359.0)
                .setSpeed(config.minSpeed, config.maxSpeed)
                .setFadeOutEnabled(true)
                .setTimeToLive(config.timeToLive)
                .addShapes(*config.shapes)
                .addSizes(Size.SMALL)
                .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f) // TODO doesn't change, always the
                .stream(300, 5000L)
    }

    fun color(resId: Int): Int {
        return ContextCompat.getColor(applicationContext, resId)
    }


}
