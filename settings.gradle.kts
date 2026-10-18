pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// Library modules
include(
    ":konfetti:xml",
    ":konfetti:compose",
    ":konfetti:core",
    ":konfetti:image"
)
// Benchmark modules
include(":konfetti:benchmark")
// Sample modules
include(
    ":samples:compose-kotlin",
    ":samples:xml-kotlin",
    ":samples:xml-java",
    ":samples:shared",
    ":samples:multiplatform:composeApp",
    ":samples:multiplatform:androidApp",
)

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
