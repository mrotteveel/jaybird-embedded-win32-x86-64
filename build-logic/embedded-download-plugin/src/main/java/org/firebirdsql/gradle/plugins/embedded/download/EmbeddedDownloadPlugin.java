package org.firebirdsql.gradle.plugins.embedded.download;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("unused")
@NullMarked
public class EmbeddedDownloadPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        EmbeddedDownloadExtension extension = project.getExtensions()
                .create("firebirdEmbedded", EmbeddedDownloadExtension.class);

        TaskProvider<EmbeddedDownloadTask> embeddedDownloadTask = project.getTasks()
                .register("downloadFirebirdEmbedded", EmbeddedDownloadTask.class);
        embeddedDownloadTask.configure(task -> {
            task.setGroup("Build");
            task.setDescription("Downloads the Firebird Embedded binary package");

            task.getDownloadUrl().set(extension.getDownloadUrl());
            task.getSha256().set(extension.getSha256());
        });

        TaskProvider<Copy> extractFirebirdEmbedded = project.getTasks().register("extractFirebirdEmbedded", Copy.class);
        extractFirebirdEmbedded.configure(task -> {
            task.into(project.getLayout().getBuildDirectory().dir("generated-sources/resources"));
            task.into(extension.getPackage().map(pkg -> pkg.replace('.', '/')), spec ->
                    spec.from(project.zipTree(embeddedDownloadTask.map(EmbeddedDownloadTask::getDownloadedFile)))
                            .include("intl/*",
                                    "plugins/*",
                                    "plugins/udr/udf_compat.dll",
                                    "tzdata/*",
                                    "*.dll",
                                    "firebird.msg",
                                    "icu*.dat",
                                    "*License.txt",
                                    "plugins.conf"));
        });

        TaskProvider<GenerateEmbeddedProviderTask> generateEmbeddedProviderTask =
                project.getTasks().register("generateEmbeddedProvider", GenerateEmbeddedProviderTask.class);
        generateEmbeddedProviderTask.configure(task -> {
            task.setGroup("Build");
            task.setDescription("Generates the Firebird Embedded provider class and registration");

            task.getEmbeddedBinaryDir().set(
                    project.getLayout().dir(extractFirebirdEmbedded.map(Copy::getDestinationDir)));
            task.getPackage().set(extension.getPackage());
            task.getVersion().set(extension.getVersion());
        });

        SourceSetContainer sourceSets = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets();
        SourceSet mainContainer = sourceSets.getAt("main");
        mainContainer.getJava().srcDir(
                generateEmbeddedProviderTask.map(GenerateEmbeddedProviderTask::getGeneratedSourcesDir));
        mainContainer.getResources().srcDir(
                generateEmbeddedProviderTask.map(GenerateEmbeddedProviderTask::getResourcesDir));
    }
}
