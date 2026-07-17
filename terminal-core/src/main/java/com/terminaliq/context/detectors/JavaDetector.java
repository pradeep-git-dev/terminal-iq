package com.terminaliq.context.detectors;

import com.terminaliq.context.ProjectContext;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaDetector implements ProjectDetector {
    @Override
    public void detect(Path directory, ProjectContext context) {
        if (directory == null) {
            return;
        }
        boolean hasPom = Files.exists(directory.resolve("pom.xml"));
        boolean hasGradle = Files.exists(directory.resolve("build.gradle"));
        boolean hasGradleKts = Files.exists(directory.resolve("build.gradle.kts"));

        context.setMavenProject(hasPom);
        context.setGradleProject(hasGradle || hasGradleKts);
    }
}
