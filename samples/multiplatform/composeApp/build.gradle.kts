import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
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

kotlin {
    // AGP 9 wants the Android app in its own module, see androidApp
    android {
        namespace = "nl.dionsegijn.samples.multiplatform.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = 23

        // Compose resources, like the heart, ship as Android assets
        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm()
    // The iOS app in iosApp embeds this framework
    listOf(iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    // The website, see wasmJsMain/resources/index.html
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(path = ":konfetti:compose"))
            implementation(project(path = ":konfetti:json"))
            implementation(project(path = ":samples:playground"))

            implementation(libs.cmp.foundation)
            implementation(libs.cmp.ui)
            implementation(libs.cmp.material)
            implementation(libs.cmp.resources)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "nl.dionsegijn.samples.multiplatform.MainKt"
        // Skiko loads native code, this keeps the JDK from warning about it on start
        jvmArgs += "--enable-native-access=ALL-UNNAMED"
    }
}

compose.resources {
    packageOfResClass = "nl.dionsegijn.samples.multiplatform.resources"
}
