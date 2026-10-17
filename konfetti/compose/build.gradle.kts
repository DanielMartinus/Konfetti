plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
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
        googleJavaFormat("1.36.1")
        target("**/*.java")
    }
}

android {
    compileSdk = buildVersions.compileSdk

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    namespace = "nl.dionsegijn.konfetti.compose"

    publishing {
        singleVariant("release")
    }
}

dependencies {
    api(project(path = ":konfetti:core"))
    api(project(path = ":konfetti:image"))

    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    // Compose UI 1.4.3 already uses this at runtime. It has to be declared here
    // so LocalLifecycleOwner is on the compile classpath. The catalog version
    // of lifecycle-runtime does not exist.
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.1")

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.compose.ui)
}
