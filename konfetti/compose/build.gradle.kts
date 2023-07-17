plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.diffplug.spotless")
}

NexusConfig.PUBLISH_ARTIFACT_ID = "konfetti-compose"
apply(from = "../../scripts/publish-module.gradle.kts")

spotless {
    kotlin {
        ktlint("0.37.2")
        target("src/**/*.kt")
    }
    java {
        removeUnusedImports()
        googleJavaFormat("1.5")
        target("**/*.java")
    }
}

android {
    compileSdk = 33
    buildToolsVersion = "34.0.0"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        minSdk = 21
        targetSdk = 33

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

    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
}
