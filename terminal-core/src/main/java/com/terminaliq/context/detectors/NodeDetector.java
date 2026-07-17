package com.terminaliq.context.detectors;

import com.terminaliq.context.ProjectContext;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeDetector implements ProjectDetector {
    @Override
    public void detect(Path directory, ProjectContext context) {
        if (directory == null) {
            return;
        }
        boolean hasPackageJson = Files.exists(directory.resolve("package.json"));
        context.setNodeProject(hasPackageJson);
    }
}
