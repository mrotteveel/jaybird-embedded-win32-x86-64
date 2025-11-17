plugins {
    `java-gradle-plugin`
}

group = "org.firebirdsql.gradle.plugins"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("org.jspecify:jspecify:1.0.0")
}

gradlePlugin {
    plugins {
        create("embeddedDownload") {
            id = "org.firebirdsql.embedded-download"
            implementationClass = "org.firebirdsql.gradle.plugins.embedded.download.EmbeddedDownloadPlugin"
        }
    }
}
