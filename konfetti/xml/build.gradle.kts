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
    coordinates(artifactId = "konfetti-xml")
    pom { name.set("konfetti-xml") }
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
    java {
        removeUnusedImports()
        googleJavaFormat("1.36.1")
        target("**/*.java")
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
    namespace = "nl.dionsegijn.konfetti.xml"
    lint {
        abortOnError = true
    }
}

dependencies {
    api(project(path = ":konfetti:core"))
    api(project(path = ":konfetti:image"))

    implementation(libs.kotlin.stdlib)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
}
