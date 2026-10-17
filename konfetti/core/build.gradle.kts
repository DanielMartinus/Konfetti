import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    id("kotlin")
    id("com.diffplug.spotless")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

NexusConfig.PUBLISH_ARTIFACT_ID = "konfetti-core"
apply(from = "../../scripts/publish-module.gradle.kts")

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
    java {
        removeUnusedImports()
        googleJavaFormat("1.37.0")
        target("**/*.java")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Constants.kotlinVersion}")
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
}
