import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.firebirdsql.embedded-download")
    id("nu.studer.credentials").version("3.0")
}

description = "Firebird Embedded Distribution for Jaybird for Windows x86-64"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")
val credentials = properties["credentials"] as CredentialsContainer
extra["signing.password"] = credentials.forKey("signing.password")
extra["centralPassword"] = credentials.forKey("centralPassword")

dependencies {
    compileOnly(libs.org.firebirdsql.jdbc.jaybird)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.org.firebirdsql.jdbc.jaybird)
    testRuntimeOnly(libs.jna)
}

firebirdEmbedded {
    downloadUrl = uri(
        "https://github.com/FirebirdSQL/firebird/releases/download/v5.0.3/Firebird-5.0.3.1683-0-windows-x64.zip")
    sha256 = "3e3c38fde46ff1d73cb964c1422a4535892bd2a8e5b6ea692685639a225cf3bd"
    version = "WI-V5.0.3.1683 Firebird 5.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            name = "Firebird Embedded Distribution for Jaybird for Windows x86-64"
            description = "Firebird embedded library packaged for use with Jaybird 5 and higher"
            url = "https://firebirdsql.org/"
            inceptionYear = "2023"

            developers {
                developer {
                    id = "mrotteveel"
                    name = "Mark Rotteveel"
                    email = "mark@lawinegevaar.nl"
                    roles = setOf("Administrator")
                }
            }
            licenses {
                license {
                    name = "Initial Developer's Public License Version 1.0"
                    url = "https://firebirdsql.org/en/initial-developer-s-public-license-version-1-0/"
                    distribution = "repo"
                }
            }
            scm {
                connection = "scm:git:https://github.com/mrotteveel/jaybird-embedded-win32-x86-64.git"
                developerConnection = "scm:git:git@github.com:mrotteveel/jaybird-embedded-win32-x86-64.git"
                url = "https://github.com/mrotteveel/jaybird-embedded-win32-x86-64"
            }
            issueManagement {
                system = "GitHub"
                url = "https://github.com/mrotteveel/jaybird-embedded-win32-x86-64/issues"
            }
        }
        repositories {
            maven {
                url = uri((if (extra["isReleaseVersion"] as Boolean) properties["releaseRepository"] else properties["snapshotRepository"]) as String)
                credentials {
                    username = findProperty("centralUsername") as String?
                    password = findProperty("centralPassword") as String?
                }
            }
        }
    }
}

signing {
    setRequired { (project.extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publish") }
    sign(publishing.publications["maven"])
}

tasks.withType<PublishToMavenRepository>().onEach {
    it.doFirst {
        if (findProperty("centralUsername") == null || findProperty("centralPassword") == null) {
            throw RuntimeException("No credentials for publishing, make sure to specify the properties " +
                    "credentialsPassphrase, or centralUsername and centralPassword. See devdoc/deploy.md for details.")
        }
    }
}
