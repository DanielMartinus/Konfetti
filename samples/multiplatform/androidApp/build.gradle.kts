plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}

android {
    namespace = "nl.dionsegijn.samples.multiplatform.android"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "nl.dionsegijn.samples.multiplatform"
        minSdk = 23
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    // Compose 1.11 is built for Java 11
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(path = ":samples:multiplatform:composeApp"))
    implementation(libs.androidx.activity.compose)
}
