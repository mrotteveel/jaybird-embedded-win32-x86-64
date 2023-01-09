Deploying
=========

1. Download the latest version of Firebird embedded for the Windows x86-64, and
replace the files under `src/main/resources`.
2. Update the artifact version with

   ```
   mvn versions:set -DnewVersion=4.0.2.0
   ```
   
   Where the first 3 digits are the Firebird version, and the fourth the release
of the `org.firebirdsql.embedded:jaybird-firebird-embedded-win32-x86-64` version 
for that Firebird version.
3. Commit all changes and create a signed tag using

    ```
    git tag -s v4.0.2.0 -m 'jaybird-firebird-embedded-win32-x86-64 4.0.2.0'
    ```
4. Push tag using

    ```
    git push origin v4.0.2.0
    ```

5. To deploy to Maven use

    ```
    mvn clean deploy -P release
    ```
    
    For snapshots we can forego signing and generating javadoc + sources using:
    
    ```
    mvn clean deploy
    ```

This requires the proper Sonatype credentials to be set in userhome/.m2/settings.xml.

See https://central.sonatype.org/pages/apache-maven.html for details.
 