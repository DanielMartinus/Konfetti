plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(path = ":konfetti:compose"))

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
    }
}

compose.resources {
    packageOfResClass = "nl.dionsegijn.samples.multiplatform.resources"
}
