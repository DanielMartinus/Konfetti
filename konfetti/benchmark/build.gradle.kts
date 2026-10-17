plugins {
    id("java-library")
    id("kotlin")
    id("com.diffplug.spotless")
    id("me.champeau.jmh") version "0.7.2"
}

spotless {
    kotlin {
        ktlint("1.1.0")
        target("src/**/*.kt")
    }
}

dependencies {
    jmh(project(":konfetti:core"))
    jmh(libs.kotlin.stdlib)
}

// Defaults for local runs via `./gradlew :konfetti:benchmark:jmh`.
// CI runs the jar from `jmhJar` directly and passes its own options.
jmh {
    jmhVersion.set("1.37")
    fork.set(3)
    warmupIterations.set(5)
    iterations.set(10)
    warmup.set("500ms")
    timeOnIteration.set("500ms")
    profilers.add("gc")
    resultFormat.set("JSON")
}
