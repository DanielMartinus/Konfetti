import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
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
            sourcesJar = SourcesJar.Sources(),
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

    // Built for Kotlin 2.3 so apps on Kotlin 2.2, like AGP 9's built-in Kotlin, can still use it
    coreLibrariesVersion = "2.3.0"
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
        apiVersion.set(KotlinVersion.KOTLIN_2_3)
    }

    android {
        namespace = "nl.dionsegijn.konfetti.compose"
        compileSdk = libs.versions.compileSdk.get().toInt()
        // Compose 1.11 itself needs API 23
        minSdk = 23

        // Apps only need the compileSdk Compose 1.11 asks for, not the one we build with
        aarMetadata {
            minCompileSdk = 35
        }

        // Compose 1.11 is built for Java 11, so its inline functions need the same target
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        withHostTest {}
    }
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosArm64()
    iosSimulatorArm64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(path = ":konfetti:core"))

            implementation(libs.cmp.foundation)
            implementation(libs.cmp.ui)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
        }
        // The web compilers need the stdlib they ship with
        jsMain.dependencies { implementation(libs.kotlin.stdlib) }
        wasmJsMain.dependencies { implementation(libs.kotlin.stdlib) }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            // Drawable images, shared with konfetti-xml
            api(project(path = ":konfetti:image"))
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
