plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.spotless)
}

// Konfetti for plain web pages: plays parties from json and draws them on a canvas.
// Konfetti Studio uses it, so it shows the same confetti as the apps.
kotlin {
    js {
        outputModuleName = "konfetti-web"
        browser()
        nodejs()
        useEsModules()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":konfetti:json"))
        }
        jsTest.dependencies {
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
