import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    coordinates(artifactId = "konfetti-xml")
    pom {
        name.set("konfetti-xml")
        // Maven and Bazel only read the POM, and the konfetti-core root has no classes in it
        withXml {
            val ids = asElement().getElementsByTagName("artifactId")
            for (i in 0 until ids.length) {
                if (ids.item(i).textContent == "konfetti-core") ids.item(i).textContent = "konfetti-core-jvm"
            }
        }
    }
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

kotlin {
    // Built for Kotlin 2.3 so apps on Kotlin 2.2, like AGP 9's built-in Kotlin, can still use it
    coreLibrariesVersion = "2.3.0"
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
        apiVersion.set(KotlinVersion.KOTLIN_2_3)
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

    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
}
