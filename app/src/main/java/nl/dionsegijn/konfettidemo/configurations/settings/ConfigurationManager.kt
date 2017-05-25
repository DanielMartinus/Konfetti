package nl.dionsegijn.konfettidemo.configurations.settings

/**
 * Created by dionsegijn on 5/24/17.
 */
class ConfigurationManager {

    var active: Configuration
    var configurations: List<Configuration> = listOf(
            Configuration(Configuration.TYPE_STREAM_FROM_TOP, "Top"),
            Configuration(Configuration.TYPE_DRAG_AND_SHOOT, "Drag 'n Shoot"),
            Configuration(Configuration.TYPE_BURST_FROM_CENTER, "Burst")
    )

    init {
        active = configurations[0]
    }

}
