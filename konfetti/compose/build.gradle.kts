import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

mavenPublishing {
    coordinates(artifactId = "konfetti-compose")
    pom { name.set("konfetti-compose") }
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true,
        ),
    )
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}

kotlin {
    // The binary-compatibility validator can't see the Android KMP target, Kotlin's own check can
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

    android {
        namespace = "nl.dionsegijn.konfetti.compose"
        compileSdk = libs.versions.compileSdk.get().toInt()
        // Compose 1.11 itself needs API 23
        minSdk = 23

        // Apps only need the compileSdk Compose 1.11 asks for, not the one we build with
        aarMetadata {
            minCompileSdk = 35
        }

        // Compose 1.12 is built for Java 11, so its inline functions need the same target
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        withHostTest {}
    }

    sourceSets {
        androidMain.dependencies {
            api(project(path = ":konfetti:core"))
            api(project(path = ":konfetti:image"))

            implementation(libs.cmp.foundation)
            implementation(libs.cmp.ui)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.test.junit)
        }
    }
}

// Keep the task names CI and the other modules use
tasks.register("apiCheck") { dependsOn("checkKotlinAbi") }
tasks.register("apiDump") { dependsOn("updateKotlinAbi") }
tasks.named("check") { dependsOn("checkKotlinAbi") }
