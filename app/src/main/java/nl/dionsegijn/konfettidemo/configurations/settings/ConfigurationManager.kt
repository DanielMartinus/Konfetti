package nl.dionsegijn.konfettidemo.configurations.settings

/**
 * Created by dionsegijn on 5/24/17.
 */
class ConfigurationManager {

    var active: Configuration
    var configurations: List<Configuration> = listOf(
            Configuration("Top"),
            Configuration("Drag 'n Shoot"),
            Configuration("Stream")
    )

    init {
        active = configurations[0]
    }

}
