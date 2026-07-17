package com.terminaliq.context.detectors;

import com.terminaliq.context.ProjectContext;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitDetector implements ProjectDetector {
    @Override
    public void detect(Path directory, ProjectContext context) {
        if (directory == null) {
            return;
        }
        boolean exists = Files.exists(directory.resolve(".git"));
        context.setGitRepository(exists);
    }
}
