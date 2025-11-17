package org.firebirdsql.gradle.plugins.embedded.download;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.jspecify.annotations.NullMarked;

import java.net.URI;
import java.util.regex.Pattern;

@NullMarked
public abstract class EmbeddedDownloadExtension {

    private static final Pattern VERSION_PATTERN =
            Pattern.compile("^\\w{2}-\\w(\\d+)\\.(\\d+)\\.(\\d+)\\.\\d+.*$");

    /**
     * Download URL of the Firebird embedded package.
     */
    public abstract Property<URI> getDownloadUrl();

    /**
     * Full version of the Firebird embedded package ({@code <platform>-<type><major>.<minor>.<revision>.<build>[<extra-info>}]).
     */
    public abstract Property<String> getVersion();

    /**
     * Expected SHA-256 of the Firebird embedded package.
     */
    public abstract Property<String> getSha256();

    public Provider<String> getPackage() {
        return getVersion().map(version -> {
            var versionMatcher = VERSION_PATTERN.matcher(version);
            if (!versionMatcher.matches()) {
                throw new IllegalArgumentException("Expected Firebird version in format "
                        + "<platform>-<type><major>.<minor>.<revision>.<build>[ <extra-info>}], was: " + version);
            }
            String underScoredVersion = "%s_%s_%s"
                    .formatted(versionMatcher.group(1), versionMatcher.group(2), versionMatcher.group(3));
            return "org.firebirdsql.embedded.firebird" + underScoredVersion + ".win32_x86_64";
        });
    }

}
