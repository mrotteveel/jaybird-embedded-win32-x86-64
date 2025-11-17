allprojects {
    repositories {
        mavenCentral()
    }

    group = "org.firebirdsql.embedded"
    version = "5.0.3.0-SNAPSHOT"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
