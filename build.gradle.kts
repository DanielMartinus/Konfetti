import com.vanniktech.maven.publish.MavenPublishBaseExtension
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.dokka.javadoc) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.binary.compatibility.validator)
}

// Shared Maven Central setup for the library modules that apply the publish plugin.
// Each module only sets its own artifactId.
subprojects {
    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        configure<MavenPublishBaseExtension> {
            coordinates(groupId = "nl.dionsegijn", version = Constants.konfettiVersion)
            publishToMavenCentral(automaticRelease = true)
            signAllPublications()

            pom {
                description.set("Lightweight particle system to celebrate with confetti in your app")
                url.set("https://github.com/DanielMartinus/Konfetti")

                licenses {
                    license {
                        name.set("ISC")
                        url.set("https://github.com/DanielMartinus/Konfetti/blob/main/LICENSE")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("DanielMartinus")
                        name.set("Dion Segijn")
                        email.set("danielsegijn@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/DanielMartinus/konfetti.git")
                    developerConnection.set("scm:git:ssh://github.com/DanielMartinus/konfetti.git")
                    url.set("https://github.com/DanielMartinus/konfetti/tree/main")
                }
            }
        }

        // Sign with the same keys from local.properties as the old publish script
        val properties = File(rootDir, "local.properties")
        if (properties.exists()) {
            val localProperties = properties.inputStream().use { java.util.Properties().apply { load(it) } }
            configure<SigningExtension> {
                useInMemoryPgpKeys(
                    localProperties["signing.keyId"] as String?,
                    localProperties["signing.key"] as String?,
                    localProperties["signing.password"] as String?,
                )
            }
        }
    }
}

apiValidation {
    ignoredProjects.addAll(
        listOf(
            "benchmark",
            "compose-kotlin",
            "xml-kotlin",
            "xml-java",
            "shared",
            "composeApp",
            "androidApp",
            "playground",
            // Compose checks its API with Kotlin's own abiValidation
            "compose",
        ),
    )
}

// The validator only hooks into the kotlin-android plugin, so it skips modules on AGP built-in Kotlin.
// Register its tasks by hand against the release classes. apiDump and apiCheck work the same as in core.

// Core has the validator's own runtime classpath, reuse it so the versions stay in sync
val validatorClasspath = provider { project(":konfetti:core").configurations.getByName("bcv-rt-jvm-cp-resolver") }
listOf(":konfetti:image", ":konfetti:xml").forEach { path ->
    project(path) {
        pluginManager.withPlugin("com.android.library") {
            val apiBuild =
                tasks.register<KotlinApiBuildTask>("apiBuild") {
                    inputClassesDirs.from(tasks.named<KotlinCompile>("compileReleaseKotlin").flatMap { it.destinationDirectory })
                    outputApiFile.set(layout.buildDirectory.file("api/${project.name}.api"))
                    runtimeClasspath.from(validatorClasspath)
                }
            tasks.register<Copy>("apiDump") {
                from(apiBuild.flatMap { it.outputApiFile })
                into(layout.projectDirectory.dir("api"))
            }
            val apiCheck =
                tasks.register<KotlinApiCompareTask>("apiCheck") {
                    projectApiFile.set(layout.projectDirectory.file("api/${project.name}.api"))
                    generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
                }
            tasks.named("check") { dependsOn(apiCheck) }
        }
    }
}
