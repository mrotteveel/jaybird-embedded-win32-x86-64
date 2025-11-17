package org.firebirdsql.gradle.plugins.embedded.download;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@CacheableTask
@NullMarked
public abstract class EmbeddedDownloadTask extends DefaultTask {

    public EmbeddedDownloadTask() {
        getDownloadDir().convention(getProject().getLayout().getBuildDirectory().dir("downloads"));
    }

    /**
     * Download URL of the Firebird embedded package.
     */
    @Input
    public abstract Property<URI> getDownloadUrl();

    /**
     * Expected SHA-256 of the Firebird embedded package.
     */
    @Input
    public abstract Property<String> getSha256();

    /**
     * Target directory of the download.
     */
    @Internal
    public abstract DirectoryProperty getDownloadDir();

    /**
     * Downloaded file.
     */
    @OutputFile
    public Provider<RegularFile> getDownloadedFile() {
        return getDownloadUrl().flatMap(uri -> getDownloadDir().file(
                Path.of(uri.getPath()).getFileName().toString()));
    }

    @TaskAction
    public void download() throws IOException, NoSuchAlgorithmException {
        Logger logger = getLogger();
        Path downloadFile = getDownloadedFile().get().getAsFile().toPath();
        Path downloadDir = downloadFile.getParent();
        Files.createDirectories(downloadDir);

        URI downloadUrl = getDownloadUrl().get();
        logger.lifecycle("Downloading {} to {}", downloadUrl, downloadFile);
        var sha256Digest = MessageDigest.getInstance("SHA-256");
        try (InputStream downloadStream = downloadUrl.toURL().openStream();
             var out = new DigestOutputStream(Files.newOutputStream(downloadFile), sha256Digest)) {
            downloadStream.transferTo(out);
        } catch (IOException e) {
            logger.error("Failed to download file from {}", downloadUrl);
            throw e;
        }

        String actualSha256 = HexFormat.of().formatHex(sha256Digest.digest());
        String expectedSha256 = getSha256().get();
        if (!actualSha256.equals(expectedSha256)) {
            logger.error("Hash mismatch, was: {}, expected {}", actualSha256, expectedSha256);
            throw new IllegalStateException("Hash mismatch, was: %s, expected: %s (for %s)"
                    .formatted(actualSha256, expectedSha256, downloadUrl));
        }
        logger.lifecycle("Download of {} to {} completed (hash: {})", downloadUrl, downloadFile, actualSha256);
    }

}
