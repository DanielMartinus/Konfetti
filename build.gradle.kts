import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.dokka.javadoc) apply false
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.binary.compatibility.validator)
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
