package com.terminaliq.context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ShellContext {

    private Path currentDirectory;
    private final String username;

    public ShellContext() {
        // Initialize to JVM current directory
        this.currentDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        this.username = System.getProperty("user.name", "user");
    }

    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public String getUsername() {
        return username;
    }

    public void changeDirectory(String targetPath) throws IOException {
        if (targetPath == null || targetPath.trim().isEmpty()) {
            // cd without args: in Windows PowerShell it defaults to user profile directory
            String userHome = System.getProperty("user.home");
            if (userHome != null) {
                targetPath = userHome;
            } else {
                return;
            }
        }

        // Handle quotes if the user typed cd "path with spaces" or cd 'path'
        targetPath = targetPath.trim();
        if ((targetPath.startsWith("\"") && targetPath.endsWith("\"")) ||
            (targetPath.startsWith("'") && targetPath.endsWith("'"))) {
            targetPath = targetPath.substring(1, targetPath.length() - 1);
        }

        Path target = Paths.get(targetPath);
        Path newDir;

        if (target.isAbsolute()) {
            newDir = target;
        } else {
            newDir = currentDirectory.resolve(target);
        }

        newDir = newDir.toAbsolutePath().normalize();

        if (!Files.exists(newDir)) {
            throw new IOException("cd: The path does not exist: " + newDir);
        }
        if (!Files.isDirectory(newDir)) {
            throw new IOException("cd: Path is not a directory: " + newDir);
        }

        this.currentDirectory = newDir;
    }
}
