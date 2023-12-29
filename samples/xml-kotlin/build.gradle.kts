plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
    java {
        removeUnusedImports()
        googleJavaFormat("1.15.0")
        target("**/*.java")
    }
}

android {
    compileSdk = buildVersions.compileSdk

    defaultConfig {
        minSdk = 23
        targetSdk = buildVersions.targetSdk

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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcomat)
    implementation(libs.android.material)
    implementation(libs.androidx.constraintlayout)

    debugImplementation(libs.androidx.tracing)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.test.junit.ext)
}
