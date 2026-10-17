plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.spotless)
}

NexusConfig.PUBLISH_ARTIFACT_ID = "konfetti-image"
apply(from = "../../scripts/publish-module.gradle.kts")

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 15
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    // The classes keep the nl.dionsegijn.konfetti.xml.image package so existing imports work.
    // The namespace only names the generated R class, so it can't clash with xml.
    namespace = "nl.dionsegijn.konfetti.image"
    publishing {
        singleVariant("release")
    }
}

dependencies {
    api(project(path = ":konfetti:core"))
}
