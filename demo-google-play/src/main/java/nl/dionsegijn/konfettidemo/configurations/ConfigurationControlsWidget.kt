package nl.dionsegijn.konfettidemo.configurations

import android.content.Context
import android.os.Build
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.configurations.viewpager.ConfigPagerAdapter
import nl.dionsegijn.konfettidemo.configurations.viewpager.TabConfig
import nl.dionsegijn.konfettidemo.configurations.views.*
import nl.dionsegijn.konfettidemo.databinding.BottomsheetConfigControlsBinding
import nl.dionsegijn.konfettidemo.interfaces.OnConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.OnGlobalConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.UpdateConfiguration

/**
 * Created by dionsegijn on 5/21/17.
 */
class ConfigurationControlsWidget : LinearLayout, OnConfigurationChangedListener, OnGlobalConfigurationChangedListener {

    private var binding: BottomsheetConfigControlsBinding =
        BottomsheetConfigControlsBinding.inflate(LayoutInflater.from(context), this)
    var configuration = ConfigurationManager()
    var onConfigurationChanged: OnConfigurationChangedListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.bottomsheet_config_controls, this)
        orientation = VERTICAL
        isClickable = true

        initViewPager()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            elevation = 100f
        }
    }

    override fun onConfigurationChanged(selected: Configuration) {
        configuration.active = selected
        val childCount = binding.viewPager.childCount
        (0 until childCount)
                .map { binding.viewPager.getChildAt(it); }
                .filterIsInstance<UpdateConfiguration>()
                .forEach {
                    it.onUpdateConfiguration(selected)
                }
        onConfigurationChanged?.onConfigurationChanged(selected)
    }

    override fun onLimitActiveParticleSystemsChanged(limit: Boolean) {
        configuration.maxParticleSystemsAlive =
                if (limit) ConfigurationManager.PARTICLE_SYSTEMS_DEFAULT
                else ConfigurationManager.PARTICLE_SYSTEMS_INFINITE
    }

    override fun resetConfigurationsToDefaults() {
        configuration = ConfigurationManager()
        onConfigurationChanged(configuration.active)
        initViewPager()
    }

    /**
     * ViewPager setup
     */

    private fun initViewPager() {
        binding.viewPager.adapter = ConfigPagerAdapter(getTabs())
        binding.viewPager.offscreenPageLimit = getTabs().size
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.setIcon(getTabs()[i].icon)
        }
    }

    fun setOnTabSelectedListener(onTabSelectedListener: TabLayout.OnTabSelectedListener) {
        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    /* Just some simple views used in viewpager to display configuration settings */
    private fun getTabs(): Array<TabConfig> {
        return arrayOf(
                TabConfig(R.drawable.ic_configurations, ConfigTypeSelectionView(context, this, configuration)),
                TabConfig(R.drawable.ic_paint, ColorSelectionView(context, configuration)),
                TabConfig(R.drawable.ic_shapes, ShapeSelectionView(context, configuration)),
                TabConfig(R.drawable.ic_speed, SpeedSelectionView(context, configuration, "Speed", 10)),
                TabConfig(R.drawable.ic_time_to_live, TimeToLiveSelectionView(context, configuration, "Time to live", 5000)),
                TabConfig(R.drawable.ic_settings, GlobalConfigSelectionView(context, this, configuration)))
    }

}
