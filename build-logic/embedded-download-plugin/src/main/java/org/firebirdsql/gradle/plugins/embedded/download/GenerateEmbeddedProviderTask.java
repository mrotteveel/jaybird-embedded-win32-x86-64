package org.firebirdsql.gradle.plugins.embedded.download;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NullMarked
public abstract class GenerateEmbeddedProviderTask extends DefaultTask {

    public GenerateEmbeddedProviderTask() {
        getResourcesDir().convention(getEmbeddedBinaryDir());
        getGeneratedSourcesDir().convention(getProject().getLayout().getBuildDirectory().dir("generated-sources/java"));
    }

    @InputDirectory
    public abstract DirectoryProperty getEmbeddedBinaryDir();

    @Input
    public abstract Property<String> getVersion();

    @Input
    public abstract Property<String> getPackage();

    @OutputDirectory
    public abstract DirectoryProperty getGeneratedSourcesDir();

    @OutputDirectory
    public abstract DirectoryProperty getResourcesDir();

    private Provider<String> getPackageAsPath() {
        return getPackage().map(pkg -> pkg.replace('.', '/'));
    }

    @TaskAction
    public void generateProvider() throws IOException {
        Path sourcesDir = getGeneratedSourcesDir().get().getAsFile().toPath();
        String packageAsPath = getPackageAsPath().get();
        Path sourcePackageDir = sourcesDir.resolve(packageAsPath);
        Path sourceFile = sourcePackageDir.resolve("FirebirdEmbeddedProvider.java");

        getLogger().lifecycle("Generating FirebirdEmbeddedProvider class");
        Files.createDirectories(sourcePackageDir);
        try (var out = Files.newBufferedWriter(sourceFile, StandardCharsets.UTF_8)) {
            out.append("package ").append(getPackage().get()).append(";\n\n");
            out.append("""
                    import org.firebirdsql.jna.embedded.classpath.ClasspathFirebirdEmbeddedLibrary;
                    import org.firebirdsql.jna.embedded.classpath.ClasspathFirebirdEmbeddedResource;
                    import org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedLibrary;
                    import org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedLoadingException;
                    
                    import java.util.Arrays;
                    import java.util.Collection;
                    
                    public final class FirebirdEmbeddedProvider implements org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedProvider {
                    
                        @Override
                        public String getPlatform() {
                            return "win32-x86-64";
                        }
                    
                        @Override
                        public String getVersion() {
                    """).append("        return \"").append(getVersion().get()).append("\";\n");
            out.append("""
                        }
                    
                        @Override
                        public FirebirdEmbeddedLibrary getFirebirdEmbeddedLibrary() throws FirebirdEmbeddedLoadingException {
                            return ClasspathFirebirdEmbeddedLibrary.load(this, new ResourceInfo());
                        }
                    
                        private static final class ResourceInfo implements ClasspathFirebirdEmbeddedResource {
                    
                            @Override
                            public Collection<String> getResourceList() {
                                return Arrays.asList(
                    """);
            Path embeddedBinaryPackageDir = getEmbeddedBinaryDir().get().getAsFile().toPath().resolve(packageAsPath);
            String indent = " ".repeat(16);
            String resourceList = StreamSupport.stream(getEmbeddedBinaryDir().getAsFileTree()
                    .matching(it -> it.include(packageAsPath + "/**"))
                    .spliterator(), false)
                    .map(f -> {
                        Path filePath = f.toPath();
                        if (filePath.startsWith(embeddedBinaryPackageDir)) {
                            Path relativePath = embeddedBinaryPackageDir.relativize(f.toPath());
                            return relativePath.toString().replace('\\', '/');
                        } else {
                            getLogger().error("Path {} is not contained in {}; skipping", filePath, embeddedBinaryPackageDir);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\",\n" + indent + "\"", indent + "\"", "\"\n"));
            out.append(resourceList);
            out.append("""
                                );
                            }
                    
                            @Override
                            public String getLibraryEntryPoint() {
                                return "fbclient.dll";
                            }
                        }
                    }
                    """);
        }

        getLogger().lifecycle("Generating service definition");
        Path resourcesDir = getResourcesDir().get().getAsFile().toPath();
        Path serviceDefinitionFile = resourcesDir
                .resolve("META-INF/services/org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedProvider");
        Files.createDirectories(serviceDefinitionFile.getParent());
        try (var out = Files.newBufferedWriter(serviceDefinitionFile)) {
            out.append(getPackage().get()).append(".FirebirdEmbeddedProvider\n");
        }
    }

}
