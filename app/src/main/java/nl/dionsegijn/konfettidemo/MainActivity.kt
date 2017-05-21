package nl.dionsegijn.konfettidemo

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfettidemo.configurations.settings.DragBurstConfiguration
import nl.dionsegijn.konfettidemo.configurations.settings.StreamConfiguration
import nl.dionsegijn.konfettidemo.configurations.settings.TopBurstConfiguration
import nl.dionsegijn.konfettidemo.interfaces.SimpleOnTabSelectedListener

/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity() {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSectionButtons()
        setupTabSelectionBottomSheetBehavior()
        bottomSheetBehavior = BottomSheetBehavior.from(viewConfigurationControls)

    }

    fun setupSectionButtons() {
        buttonSectionBurstFromTop.setOnClickListener {
            val configuration = TopBurstConfiguration()
            // TODO implement loading config for this section
        }
        buttonSectionDragAndBurst.setOnClickListener {
            val configuration = DragBurstConfiguration()
            // TODO implement loading config for this section
        }
        buttonSectionStreamFromCenter.setOnClickListener {
            val configuration = StreamConfiguration()
            // TODO implement loading config for this section
        }
    }

    fun setupTabSelectionBottomSheetBehavior() {
        viewConfigurationControls.setOnTabSelectedListener(object : SimpleOnTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state =
                        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            BottomSheetBehavior.STATE_EXPANDED
                        } else {
                            BottomSheetBehavior.STATE_COLLAPSED
                        }
            }
        })
    }

}
