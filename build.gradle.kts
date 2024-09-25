plugins {
    id("java-library")
    id("maven-publish")
}

group = "net.lostluma"
version = project.property("version").toString()

base {
    archivesName = "battery"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.annotations)
}

java {
    withSourcesJar()

    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.withType<Jar> {
    from("LICENSE")
}

tasks.withType<AbstractArchiveTask> {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

/**
 * Builds the bundled Jar.
 *
 * Put all variants of the library into a `library/` directory first!
 */
tasks.register<Jar>("buildBundled") {
    archiveClassifier = "bundled"

    from(files("library/"))
    from(sourceSets.main.get().output)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = base.archivesName.get()
            version = version

            from(components["java"])
            artifact(tasks.named("buildBundled").get())
        }
    }

    repositories {
        maven {
            url = uri("https://maven.lostluma.net/releases")

            credentials {
                username = project.properties["maven.username"].toString()
                password = project.properties["maven.password"].toString()
            }
        }
    }
}
