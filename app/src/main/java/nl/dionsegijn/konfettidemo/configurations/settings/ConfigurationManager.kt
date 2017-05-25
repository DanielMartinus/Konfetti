package nl.dionsegijn.konfettidemo.configurations.settings

import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/24/17.
 */
class ConfigurationManager {

    var active: Configuration
    var configurations: List<Configuration> = listOf(
            Configuration(Configuration.TYPE_STREAM_FROM_TOP, "Top", R.string.stream_from_top_instructions),
            Configuration(Configuration.TYPE_DRAG_AND_SHOOT, "Drag 'n Shoot", R.string.drag_and_shoot_app_name_instructions),
            Configuration(Configuration.TYPE_BURST_FROM_CENTER, "Burst", R.string.burst_from_center_instructions)
    )

    init {
        active = configurations[0]

        /** Specific settings for TYPE_BURST_FROM_CENTER configuration */
        configurations[2].maxSpeed = 8f
        configurations[2].timeToLive = 4000L
    }

}
