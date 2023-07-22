plugins {
    id("com.android.application")
    id("kotlin-android")
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
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk=  33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    namespace = "nl.dionsegijn.xml.kotlin"
}

dependencies {
    implementation(project(path = ":konfetti:xml"))
    implementation(project(path = ":samples:shared"))
    implementation(lib.androidx.core.ktx)
    implementation(lib.androidx.appcomat)
    implementation(lib.android.material)
    implementation(lib.androidx.constraintlayout)
}
