import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("org.jetbrains.dokka") version "2.2.0" apply false
    id("org.jetbrains.dokka-javadoc") version "2.2.0" apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.2"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Constants.kotlinVersion}")
        classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:${Constants.kotlinVersion}")
        classpath("com.diffplug.spotless:spotless-plugin-gradle:8.10.4")
        classpath("io.github.gradle-nexus:publish-plugin:2.0.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
        classpath("org.jetbrains.dokka-javadoc:org.jetbrains.dokka-javadoc.gradle.plugin:2.2.0")
    }
}

// Connect with the repository using properties from local.properties in the root of the project
val properties = File(rootDir, "local.properties")
if(properties.exists()) {
    val localProperties = properties.inputStream().use { java.util.Properties().apply { load(it) } }
    // Set up Sonatype repository
    nexusPublishing {
        repositories {
            sonatype {
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
                packageGroup.set("nl.dionsegijn")
                stagingProfileId.set(localProperties["sonatypeStagingProfileId"] as String?)
                username.set(localProperties["ossrhUsername"] as String?)
                password.set(localProperties["ossrhPassword"] as String?)
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
        ),
    )
}

// The validator only hooks into the kotlin-android plugin, so it skips modules on AGP built-in Kotlin.
// Register its tasks by hand against the release classes. apiDump and apiCheck work the same as in core.

// Core has the validator's own runtime classpath, reuse it so the versions stay in sync
val validatorClasspath = provider { project(":konfetti:core").configurations.getByName("bcv-rt-jvm-cp-resolver") }
listOf(":konfetti:image", ":konfetti:compose", ":konfetti:xml").forEach { path ->
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
