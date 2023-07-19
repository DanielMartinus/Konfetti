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

pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("lib") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}
