// Library modules
include(
    ":konfetti:xml",
    ":konfetti:compose",
    ":konfetti:core"
)
// Sample modules
include(
    ":samples:compose-kotlin",
    ":samples:xml-kotlin",
    ":samples:xml-java",
    ":samples:shared"
)

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
