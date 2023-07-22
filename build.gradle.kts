plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Constants.kotlinVersion}")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("com.diffplug.spotless:spotless-plugin-gradle:5.14.2")
        classpath("io.github.gradle-nexus:publish-plugin:1.1.0")
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
                stagingProfileId.set(localProperties["sonatypeStagingProfileId"] as String)
                username.set(localProperties["ossrhUsername"] as String)
                password.set(localProperties["ossrhPassword"] as String)
            }
        }
    }
}
