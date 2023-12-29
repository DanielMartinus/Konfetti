plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.diffplug.spotless")
}

NexusConfig.PUBLISH_ARTIFACT_ID = "konfetti-xml"
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
        minSdk = 15
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    namespace = "nl.dionsegijn.konfetti.xml"
    lint {
        abortOnError = true
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    debugApi(project(path = ":konfetti:core"))
    releaseApi("nl.dionsegijn:konfetti-core:${Constants.konfettiVersion}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Constants.kotlinVersion}")

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
}
