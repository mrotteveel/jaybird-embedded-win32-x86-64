# Firebird Embedded Windows x86-64 for Jaybird

[![MavenCentral](https://maven-badges.sml.io/sonatype-central/org.firebirdsql.embedded/jaybird-firebird-embedded-win32-x86-64/badge.svg)](https://maven-badges.sml.io/sonatype-central/org.firebirdsql.embedded/jaybird-firebird-embedded-win32-x86-64/)

This is an experimental library to provide Firebird embedded on the classpath of 
Java applications. It requires Jaybird 5.0.0 or higher.

For now, we only provide releases for Windows x86-64, but we will try to release
a variant for Linux x86-64 in the future.

NOTE: This feature is experimental in Jaybird 5, and may be removed or radically
changed in future versions.

Usage
-----

To use this bundle, you need to depend on this library, your preferred
Jaybird 5 (or higher) version, and the JNA version required by that version of
Jaybird:

```xml
<dependencies>
    <dependency>
        <groupId>org.firebirdsql.embedded</groupId>
        <artifactId>jaybird-firebird-embedded-win32-x86-64</artifactId>
        <version>4.0.4.0-alpha-1</version>
    </dependency>
    <dependency>
        <groupId>org.firebirdsql.jdbc</groupId>
        <artifactId>jaybird</artifactId>
        <version>5.0.3.java11</version>
    </dependency>
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna</artifactId>
        <version>5.12.1</version>
    </dependency>
</dependencies>
```

You can now use the embedded protocol without having Firebird embedded installed:

```java
public class Example {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:firebirdsql:embedded:mydb.fdb", "user", "")) {
            // use connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

Build information
-----------------

### Version ###

The version has 4 components. The first three are the Firebird version that
sourced the libraries (eg 4.0.2). The last part is a 'build' identifier, which
should usually be 0. The 'build' identifier may be incremented for patches or
new platforms added.

Suffixes like `-alpha-1` apply to this library, and not Firebird itself. It
intends to reflect that the library is still experimental.
