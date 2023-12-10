object buildVersions {
    const val compileSdk = 34
    const val targetSdk = 34
}

object Constants {
    const val composeVersion = "1.4.3"
    const val konfettiVersion = "2.0.4"
    const val kotlinVersion = "1.8.10"
}

object NexusConfig {
    const val PUBLISH_GROUP_ID = "nl.dionsegijn"
    const val PUBLISH_VERSION = Constants.konfettiVersion
    var PUBLISH_ARTIFACT_ID = ""
}