import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}

// The playground: a timeline and live editing for parties, shared by every demo and the website
kotlin {
    android {
        namespace = "nl.dionsegijn.samples.playground"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = 23

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(path = ":konfetti:compose"))

            implementation(libs.cmp.foundation)
            implementation(libs.cmp.ui)
            implementation(libs.cmp.material)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
