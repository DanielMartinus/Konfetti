plugins {
    id("com.android.application")
}

android {
    compileSdk = 33
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 17
        targetSdk = 33

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
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
}
