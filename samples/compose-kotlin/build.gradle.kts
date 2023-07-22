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
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Constants.composeVersion
    }
    namespace = "nl.dionsegijn.xml.compose"
}

dependencies {
    val composeVersion: String = Constants.composeVersion

    implementation(project(path = ":konfetti:compose"))
    implementation(project(path = ":samples:shared"))

    implementation(lib.androidx.core.ktx)
    implementation(lib.androidx.appcomat)
    implementation(lib.android.material)
    implementation(lib.androidx.activity.compose)

    implementation(lib.androidx.lifecycle.runtime)
    implementation(lib.androidx.lifecycle.livedata.ktx)

    implementation(lib.compose.ui)
    implementation(lib.compose.ui.tooling)
    implementation(lib.compose.material)
    implementation(lib.compose.runtime)
    implementation(lib.compose.runtime.livedata)
}
