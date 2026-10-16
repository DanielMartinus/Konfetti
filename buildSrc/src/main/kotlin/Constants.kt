object buildVersions {
    const val compileSdk = 34
    const val targetSdk = 34
}

object Constants {
    const val konfettiVersion = "2.0.6"
    const val kotlinVersion = "2.4.21"
}

object NexusConfig {
    const val PUBLISH_GROUP_ID = "nl.dionsegijn"
    const val PUBLISH_VERSION = Constants.konfettiVersion
    var PUBLISH_ARTIFACT_ID = ""
}