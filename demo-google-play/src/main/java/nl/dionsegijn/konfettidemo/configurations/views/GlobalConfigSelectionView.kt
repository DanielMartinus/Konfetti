package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_section_global_config_selection.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.OnGlobalConfigurationChangedListener

/**
 * Created by dionsegijn on 5/21/17.
 */
@SuppressLint("ViewConstructor")
class GlobalConfigSelectionView(
        context: Context,
        private val globalConfiglistener: OnGlobalConfigurationChangedListener,
        private val configurationManager: ConfigurationManager
) : LinearLayout(context) {

    init {
        inflate(context, R.layout.view_section_global_config_selection, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER

        setLimitActiveSystemsCheckBox()
        settingsLimitActiveSystems.setOnCheckedChangeListener { _, isChecked ->
            globalConfiglistener.onLimitActiveParticleSystemsChanged(isChecked)
        }

        settingsResetToDefaults.setOnClickListener {
            globalConfiglistener.resetConfigurationsToDefaults()
            setLimitActiveSystemsCheckBox() // Refresh checkbox
        }
    }

    private fun setLimitActiveSystemsCheckBox() {
        settingsLimitActiveSystems.isChecked = configurationManager.maxParticleSystemsAlive ==
                ConfigurationManager.PARTICLE_SYSTEMS_DEFAULT
    }

}
