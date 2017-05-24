package nl.dionsegijn.konfettidemo.interfaces

import nl.dionsegijn.konfettidemo.configurations.settings.Configuration

/**
 * Created by dionsegijn on 5/24/17.
 */
interface UpdateConfiguration {
    fun onUpdateConfiguration(configuration: Configuration)
}
