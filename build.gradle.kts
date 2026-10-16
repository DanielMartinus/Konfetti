plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("org.jetbrains.dokka") version "2.2.0" apply false
    id("org.jetbrains.dokka-javadoc") version "2.2.0" apply false
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
