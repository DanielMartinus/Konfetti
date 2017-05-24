package nl.dionsegijn.konfettidemo.configurations

import nl.dionsegijn.konfettidemo.configurations.settings.Configuration

/**
 * Created by dionsegijn on 5/24/17.
 */
class Configurations {

    var active: Configuration
    var configurations: List<Configuration> = listOf(
            Configuration(""), // TODO add icon
            Configuration(""),
            Configuration("")
    )

    init {
        active = configurations[0]
    }

}
