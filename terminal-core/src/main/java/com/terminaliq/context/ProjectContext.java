package com.terminaliq.context;

import java.nio.file.Path;

public class ProjectContext {
    private Path currentDirectory;
    private ProjectType projectType = ProjectType.UNKNOWN;

    private boolean gitRepository;
    private boolean dockerProject;
    private boolean mavenProject;
    private boolean gradleProject;
    private boolean nodeProject;
    private boolean pythonProject;

    private String projectName;

    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public boolean isGitRepository() {
        return gitRepository;
    }

    public void setGitRepository(boolean gitRepository) {
        this.gitRepository = gitRepository;
    }

    public boolean isDockerProject() {
        return dockerProject;
    }

    public void setDockerProject(boolean dockerProject) {
        this.dockerProject = dockerProject;
    }

    public boolean isMavenProject() {
        return mavenProject;
    }

    public void setMavenProject(boolean mavenProject) {
        this.mavenProject = mavenProject;
    }

    public boolean isGradleProject() {
        return gradleProject;
    }

    public void setGradleProject(boolean gradleProject) {
        this.gradleProject = gradleProject;
    }

    public boolean isNodeProject() {
        return nodeProject;
    }

    public void setNodeProject(boolean nodeProject) {
        this.nodeProject = nodeProject;
    }

    public boolean isPythonProject() {
        return pythonProject;
    }

    public void setPythonProject(boolean pythonProject) {
        this.pythonProject = pythonProject;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
