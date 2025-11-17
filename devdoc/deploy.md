Deploying
=========

1. In `win32-x86-64/build.gradle.kts`, update the `firebirdEmbedded` block with 
   the right information for:
   - `downloadUrl`: the download URL of the Firebird Windows x64 zip
   - `sha256`: the SHA-256 hash of the Firebird Windows x64 zip in `downloadUrl`
   - `version`: The version specifier (format `<platform>-<type><major>.<minor>.<revision>.<build>[<extra-info>]`, e.g. `WI-V5.0.3.1683 Firebird 5.0`)
2. Update the artifact version in the root `build.gradle.kts`
   
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
    ./gradlew publish -PcredentialsPassphrase=<credentials password>
    ```
    
    For snapshots we can forego signing and generating javadoc + sources using:
    
    ```
    ./gradlew publish
    ```

This requires the proper Sonatype credentials to be set, see also the next sectuib.

Publishing
----------

To publish to Maven use

```
gradlew clean publish -PcredentialsPassphrase=<credentials password>
```
Where `<credentials password>` is the password used to add the credentials (see
also below).

Publishing to Maven Central (non-SNAPSHOT releases) requires the following
additional steps:

1. Promote the published artifacts to Central Portal through the SwaggerUI <https://ossrh-staging-api.central.sonatype.com/swagger-ui/>
2. An explicit close through <https://central.sonatype.com/publishing/deployments>.

To be able to deploy, you need the following:

a `<homedir>/.gradle/gradle.properties` with the following properties:

```
signing.keyId=<gpg key id>
signing.secretKeyRingFile=<path to your secring.gpg> 

centralUsername=<Central Portal usertoken name>
```

In addition, you need to set the following credentials

```
./gradlew addCredentials --key signing.password --value <your secret key password> -PcredentialsPassphrase=<credentials password> 
./gradlew addCredentials --key centralPassword --value <your Central Portal usertoken password> -PcredentialsPassphrase=<credentials password> 
```

See https://github.com/etiennestuder/gradle-credentials-plugin for details on
credentials.

See https://central.sonatype.org/publish/publish-portal-maven/ for details on
Maven publishing.
 