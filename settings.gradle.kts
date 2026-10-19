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
    ":konfetti:image",
    ":konfetti:json",
    ":konfetti:web",
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
    ":samples:playground",
)

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
