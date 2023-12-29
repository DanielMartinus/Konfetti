apply(plugin = "maven-publish")
apply(plugin = "signing")
apply(plugin = "org.jetbrains.dokka")

val PLUGIN_ANDROID_LIBRARY = "com.android.library"
val PUBLICATION_NAME = "release"

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")

    if (project.plugins.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
        val libExt = checkNotNull(project.extensions.findByType(com.android.build.gradle.LibraryExtension::class.java))
        val libMainSourceSet = libExt.sourceSets.getByName("main")

        from(libMainSourceSet.java.srcDirs)
    } else {
        val sourceSetExt = checkNotNull(project.extensions.findByType(SourceSetContainer::class.java))
        val mainSourceSet = sourceSetExt.getByName("main")

        from(mainSourceSet.java.srcDirs)
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")

    val dokkaJavadocTask = tasks.getByName("dokkaJavadoc")

    from(dokkaJavadocTask)
    dependsOn(dokkaJavadocTask)
}

val group = NexusConfig.PUBLISH_GROUP_ID
val version = NexusConfig.PUBLISH_VERSION

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>(PUBLICATION_NAME) {
                // The coordinates of the library, being set from variables that
                // we'll set up later
                groupId = NexusConfig.PUBLISH_GROUP_ID
                artifactId = NexusConfig.PUBLISH_ARTIFACT_ID
                version = NexusConfig.PUBLISH_VERSION

                // Two artifacts, the `aar` (or `jar`) and the sources
                if (project.plugins.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
                    from(components[PUBLICATION_NAME])
                } else {
                    from(components["java"])
                }

                artifact(sourcesJar.get())
                artifact(javadocJar.get())

                // Mostly self-explanatory metadata
                pom {
                    name.set(NexusConfig.PUBLISH_ARTIFACT_ID)
                    description.set("Lightweight particle system to celebrate with confetti in your app")
                    url.set("https://github.com/DanielMartinus/Konfetti")

                    licenses {
                        license {
                            name.set("Konfetti License")
                            url.set("https://github.com/DanielMartinus/Konfetti/blob/main/LICENSE")
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
        }
    }

    configure<SigningExtension> {
        // Get signing properties
        val properties = File(rootDir, "local.properties")
        if(properties.exists()) {
            val localProperties = properties.inputStream().use {
                java.util.Properties().apply { load(it) }
            }
            val signingKeyId: String? = localProperties.getOrDefault("signing.keyId", null) as String?
            val signingKey: String? = localProperties.getOrDefault("signing.key", null) as String?
            val signingPassword: String? = localProperties.getOrDefault("signing.password", null) as String?
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        }

        // Start signing publication
        val pubExt = checkNotNull(extensions.findByType(PublishingExtension::class.java))
        val publication = pubExt.publications[PUBLICATION_NAME]
        sign(publication)
    }
}

