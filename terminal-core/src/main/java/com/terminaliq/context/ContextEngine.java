package com.terminaliq.context;

import com.terminaliq.context.detectors.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContextEngine {
    private final List<ProjectDetector> detectors;
    private ProjectContext cachedContext;

    public ContextEngine() {
        this.detectors = new ArrayList<>();
        this.detectors.add(new GitDetector());
        this.detectors.add(new JavaDetector());
        this.detectors.add(new NodeDetector());
        this.detectors.add(new PythonDetector());
        this.detectors.add(new DockerDetector());
    }

    public ProjectContext getCachedContext() {
        return cachedContext;
    }

    public ProjectContext refresh(Path directory) {
        if (directory == null) {
            return null;
        }
        ProjectContext context = new ProjectContext();
        context.setCurrentDirectory(directory);
        
        Path fileName = directory.getFileName();
        context.setProjectName(fileName != null ? fileName.toString() : "");

        for (ProjectDetector detector : detectors) {
            detector.detect(directory, context);
        }

        context.setProjectType(determineProjectType(context));
        this.cachedContext = context;
        return context;
    }

    private ProjectType determineProjectType(ProjectContext context) {
        int primaryCount = 0;
        ProjectType detectedType = ProjectType.UNKNOWN;

        if (context.isMavenProject()) {
            primaryCount++;
            detectedType = ProjectType.JAVA_MAVEN;
        }
        if (context.isGradleProject()) {
            primaryCount++;
            detectedType = ProjectType.JAVA_GRADLE;
        }
        if (context.isNodeProject()) {
            primaryCount++;
            detectedType = ProjectType.NODE;
        }
        if (context.isPythonProject()) {
            primaryCount++;
            detectedType = ProjectType.PYTHON;
        }

        if (primaryCount > 1) {
            return ProjectType.MULTI_PROJECT;
        } else if (primaryCount == 1) {
            return detectedType;
        } else {
            if (context.isDockerProject()) {
                return ProjectType.DOCKER;
            }
            return ProjectType.UNKNOWN;
        }
    }
}
