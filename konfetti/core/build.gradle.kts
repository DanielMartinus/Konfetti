import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.spotless)
}

kotlin {
    // Android, desktop, the XML view and the Java sample all use the jvm target
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
        nodejs()
    }
    js {
        browser()
        nodejs()
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}
