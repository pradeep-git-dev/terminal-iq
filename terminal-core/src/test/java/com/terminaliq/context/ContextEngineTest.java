package com.terminaliq.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ContextEngineTest {

    private ContextEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ContextEngine();
    }

    @Test
    public void testEmptyFolder(@TempDir Path tempDir) {
        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.UNKNOWN, context.getProjectType());
        assertFalse(context.isGitRepository());
        assertFalse(context.isDockerProject());
        assertFalse(context.isMavenProject());
        assertFalse(context.isGradleProject());
        assertFalse(context.isNodeProject());
        assertFalse(context.isPythonProject());
    }

    @Test
    public void testGitRepositoryOnly(@TempDir Path tempDir) throws IOException {
        Files.createDirectory(tempDir.resolve(".git"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.UNKNOWN, context.getProjectType());
        assertTrue(context.isGitRepository());
        assertFalse(context.isDockerProject());
        assertFalse(context.isMavenProject());
    }

    @Test
    public void testMavenProject(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("pom.xml"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.JAVA_MAVEN, context.getProjectType());
        assertTrue(context.isMavenProject());
        assertFalse(context.isGradleProject());
    }

    @Test
    public void testGradleProjectWithBuildGradle(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("build.gradle"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.JAVA_GRADLE, context.getProjectType());
        assertTrue(context.isGradleProject());
    }

    @Test
    public void testGradleProjectWithBuildGradleKts(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("build.gradle.kts"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.JAVA_GRADLE, context.getProjectType());
        assertTrue(context.isGradleProject());
    }

    @Test
    public void testNodeProject(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("package.json"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.NODE, context.getProjectType());
        assertTrue(context.isNodeProject());
    }

    @Test
    public void testPythonProjectWithRequirements(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("requirements.txt"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.PYTHON, context.getProjectType());
        assertTrue(context.isPythonProject());
    }

    @Test
    public void testPythonProjectWithPyproject(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("pyproject.toml"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.PYTHON, context.getProjectType());
        assertTrue(context.isPythonProject());
    }

    @Test
    public void testDockerProjectOnly(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("Dockerfile"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.DOCKER, context.getProjectType());
        assertTrue(context.isDockerProject());
    }

    @Test
    public void testDockerAndNodeProject(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("package.json"));
        Files.createFile(tempDir.resolve("Dockerfile"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.NODE, context.getProjectType());
        assertTrue(context.isNodeProject());
        assertTrue(context.isDockerProject());
    }

    @Test
    public void testMultiProject(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("package.json"));
        Files.createFile(tempDir.resolve("pom.xml"));

        ProjectContext context = engine.refresh(tempDir);
        assertNotNull(context);
        assertEquals(ProjectType.MULTI_PROJECT, context.getProjectType());
        assertTrue(context.isNodeProject());
        assertTrue(context.isMavenProject());
    }
}
