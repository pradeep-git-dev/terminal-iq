package com.terminaliq.context.detectors;

import com.terminaliq.context.ProjectContext;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonDetector implements ProjectDetector {
    @Override
    public void detect(Path directory, ProjectContext context) {
        if (directory == null) {
            return;
        }
        boolean hasRequirements = Files.exists(directory.resolve("requirements.txt"));
        boolean hasPyproject = Files.exists(directory.resolve("pyproject.toml"));

        context.setPythonProject(hasRequirements || hasPyproject);
    }
}
