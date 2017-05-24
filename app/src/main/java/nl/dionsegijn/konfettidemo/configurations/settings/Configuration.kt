package nl.dionsegijn.konfettidemo.configurations.settings

/**
 * Created by dionsegijn on 5/21/17.
 */
open class Configuration(title: String) {

    /**
     * The icon used when switching between configurations
     */
    val configIconResId: Int = 0
    val title: String = ""

    var timeToLive: Long = 2000

}
