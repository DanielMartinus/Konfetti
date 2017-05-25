package nl.dionsegijn.konfettidemo.configurations

import android.content.Context
import android.os.Build
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.bottomsheet_config_controls.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.selection_views.*
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.configurations.viewpager.ConfigPagerAdapter
import nl.dionsegijn.konfettidemo.configurations.viewpager.TabConfig
import nl.dionsegijn.konfettidemo.interfaces.OnConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration

/**
 * Created by dionsegijn on 5/21/17.
 */
class ConfigurationControlsWidget : LinearLayout, OnConfigurationChangedListener {

    val configuration = ConfigurationManager()
    var onConfigurationChanged: OnConfigurationChangedListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.bottomsheet_config_controls, this)
        orientation = VERTICAL
        isClickable = true

        viewPager.adapter = ConfigPagerAdapter(getTabs())
        viewPager.offscreenPageLimit = getTabs().size
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0..tabLayout.tabCount - 1) {
            tabLayout.getTabAt(i)?.setIcon(getTabs()[i].icon)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            elevation = 100f
        }
    }

    fun setOnConfigurationChangedListener(listener: OnConfigurationChangedListener) {
        this.onConfigurationChanged = listener
    }

    override fun onConfigurationChanged(selected: Configuration) {
        configuration.active = selected
        val childCount = viewPager.childCount
        (0..childCount -1)
                .map { viewPager.getChildAt(it);  }
                .filterIsInstance<UpdateConfiguration>()
                .forEach {
                    it.onUpdateConfiguration(selected)
                }
        onConfigurationChanged?.onConfigurationChanged(selected)
    }

    fun setOnTabSelectedListener(onTabSelectedListener: TabLayout.OnTabSelectedListener) {
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    /* Just some simple views used in viewpager to display configuration settings */
    fun getTabs(): Array<TabConfig> {
        return arrayOf(
                TabConfig(R.drawable.ic_settings, ConfigTypeSelectionView(context, this, configuration)),
                TabConfig(R.drawable.ic_paint, ColorSelectionView(context, configuration)),
                TabConfig(R.drawable.ic_shapes, ShapeSelectionView(context, configuration)),
                TabConfig(R.drawable.ic_speed, MultiSeekbarSelectionView(context, configuration, "Speed", 1, 10, speedValuesChanged())),
                TabConfig(R.drawable.ic_time_to_live, SeekbarSelectionView(context, configuration,  "Time to live", 5000)))
    }

    fun speedValuesChanged() : MultiSeekbarSelectionView.OnMultiSeekBarValueChanged {
        return object : MultiSeekbarSelectionView.OnMultiSeekBarValueChanged {
            override fun onValueChanged(min: Float, max: Float) {
                configuration.active.minSpeed = min
                configuration.active.maxSpeed = max
            }
        }
    }

}
