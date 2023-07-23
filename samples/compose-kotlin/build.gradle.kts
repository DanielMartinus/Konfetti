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
    compileSdk = buildVersions.compileSdk

    defaultConfig {
        minSdk = 21
        targetSdk = buildVersions.targetSdk

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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcomat)
    implementation(libs.android.material)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.material)
    implementation(libs.compose.runtime)
    implementation(libs.compose.runtime.livedata)
}
