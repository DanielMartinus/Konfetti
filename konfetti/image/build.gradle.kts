import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    coordinates(artifactId = "konfetti-image")
    pom { name.set("konfetti-image") }
    configure(
        AndroidSingleVariantLibrary(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"),
            sourcesJar = SourcesJar.Sources(),
            variant = "release",
        ),
    )
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 15

        // Apps only need the compileSdk they needed on 2.x, not the one we build with
        aarMetadata {
            minCompileSdk = 34
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    // The classes keep the nl.dionsegijn.konfetti.xml.image package so existing imports work.
    // The namespace only names the generated R class, so it can't clash with xml.
    namespace = "nl.dionsegijn.konfetti.image"
}

dependencies {
    api(project(path = ":konfetti:core"))
}
