package com.terminaliq.context.detectors;

import com.terminaliq.context.ProjectContext;
import java.nio.file.Files;
import java.nio.file.Path;

public class DockerDetector implements ProjectDetector {
    @Override
    public void detect(Path directory, ProjectContext context) {
        if (directory == null) {
            return;
        }
        boolean hasDockerfile = Files.exists(directory.resolve("Dockerfile"));
        boolean hasComposeYml = Files.exists(directory.resolve("docker-compose.yml"));
        boolean hasComposeYaml = Files.exists(directory.resolve("compose.yaml"));

        context.setDockerProject(hasDockerfile || hasComposeYml || hasComposeYaml);
    }
}
