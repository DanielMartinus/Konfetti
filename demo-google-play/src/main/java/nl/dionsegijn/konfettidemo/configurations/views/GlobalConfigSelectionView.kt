package nl.dionsegijn.konfettidemo.configurations.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.databinding.ViewSectionGlobalConfigSelectionBinding
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

    private var binding: ViewSectionGlobalConfigSelectionBinding =
        ViewSectionGlobalConfigSelectionBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        setLimitActiveSystemsCheckBox()
        binding.settingsLimitActiveSystems.setOnCheckedChangeListener { _, isChecked ->
            globalConfiglistener.onLimitActiveParticleSystemsChanged(isChecked)
        }

        binding.settingsResetToDefaults.setOnClickListener {
            globalConfiglistener.resetConfigurationsToDefaults()
            setLimitActiveSystemsCheckBox() // Refresh checkbox
        }
    }

    private fun setLimitActiveSystemsCheckBox() {
        binding.settingsLimitActiveSystems.isChecked = configurationManager.maxParticleSystemsAlive ==
                ConfigurationManager.PARTICLE_SYSTEMS_DEFAULT
    }

}
