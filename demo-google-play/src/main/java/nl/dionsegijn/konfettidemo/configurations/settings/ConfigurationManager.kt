package nl.dionsegijn.konfettidemo.configurations.settings

import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/24/17.
 */
class ConfigurationManager {
    companion object {
        @JvmStatic
        val PARTICLE_SYSTEMS_DEFAULT: Int = 6
        @JvmStatic
        val PARTICLE_SYSTEMS_INFINITE: Int = -1
    }

    var active: Configuration
    var maxParticleSystemsAlive = PARTICLE_SYSTEMS_DEFAULT
    var configurations: List<Configuration> = listOf(
            Configuration(Configuration.TYPE_STREAM_FROM_TOP, "Top", R.string.stream_from_top_instructions, R.drawable.ic_confetti_ball),
            Configuration(Configuration.TYPE_DRAG_AND_SHOOT, "Drag 'n Shoot", R.string.drag_and_shoot_app_name_instructions, R.drawable.ic_celebration),
            Configuration(Configuration.TYPE_BURST_FROM_CENTER, "Burst", R.string.burst_from_center_instructions, R.drawable.ic_fireworks)
    )

    init {
        active = configurations[0]

        /** Specific settings for TYPE_BURST_FROM_CENTER configuration */
        configurations[2].minSpeed = 1f
        configurations[2].maxSpeed = 8f
        configurations[2].timeToLive = 4000L
        configurations[2].colors = intArrayOf(R.color.lt_yellow, R.color.lt_orange,
                R.color.lt_pink, R.color.dk_cyan, R.color.dk_green)
    }

}
