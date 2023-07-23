plugins {
    id("com.android.application")
    id("com.diffplug.spotless")
}

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
    compileSdk = buildVersions.compileSdk

    defaultConfig {
        minSdk = 17
        targetSdk = buildVersions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    namespace = "nl.dionsegijn.xml.java"
    lint {
        abortOnError = true
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    implementation(project(path = ":konfetti:xml"))
    implementation(libs.androidx.appcomat)
    implementation(libs.android.material)
    implementation(libs.androidx.constraintlayout)

    debugImplementation(libs.androidx.tracing)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.test.junit.ext)
}
