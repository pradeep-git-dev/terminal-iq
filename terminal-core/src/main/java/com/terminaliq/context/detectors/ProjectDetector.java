package com.terminaliq.context.detectors;

import com.terminaliq.context.ProjectContext;
import java.nio.file.Path;

public interface ProjectDetector {
    void detect(Path directory, ProjectContext context);
}
