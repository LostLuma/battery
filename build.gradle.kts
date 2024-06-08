plugins {
    id("java-library")
    id("maven-publish")
}

group = "net.lostluma"
version = "1.0.0"

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = base.archivesName.get()
            version = version

            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://maven.lostluma.net/releases")

            credentials {
                username = project.property("maven.username").toString()
                password = project.property("maven.password").toString()
            }
        }
    }
}
