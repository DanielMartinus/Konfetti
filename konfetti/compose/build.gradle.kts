plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.diffplug.spotless")
}

NexusConfig.PUBLISH_ARTIFACT_ID = "konfetti-compose"
apply(from = "../../scripts/publish-module.gradle.kts")

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
    java {
        removeUnusedImports()
        googleJavaFormat("1.15.0")
        target("**/*.java")
    }
}

android {
    compileSdk = buildVersions.compileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Constants.composeVersion
    }
    namespace = "nl.dionsegijn.konfetti.compose"
}

dependencies {
    val composeVersion: String = Constants.composeVersion

    debugApi(project(path = ":konfetti:core"))
    releaseApi("nl.dionsegijn:konfetti-core:${Constants.konfettiVersion}")

    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.androidx.lifecycle.runtime.compose)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.compose.ui)
}
