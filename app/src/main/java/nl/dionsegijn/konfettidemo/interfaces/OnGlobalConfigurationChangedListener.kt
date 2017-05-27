package nl.dionsegijn.konfettidemo.interfaces

/**
 * Created by dionsegijn on 5/24/17.
 */
interface OnGlobalConfigurationChangedListener {

    fun onLimitActiveParticleSystemsChanged(limit: Boolean)

    fun resetConfigurationsToDefaults()

}
