package com.terminaliq.execution;

import com.terminaliq.context.ShellContext;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CommandExecutor {

    private final ShellContext context;
    private final AtomicReference<Process> activeProcess = new AtomicReference<>();

    public CommandExecutor(ShellContext context) {
        this.context = context;
    }

    public ExecutionResult execute(String command) {
        ProcessBuilder pb = new ProcessBuilder(
            "powershell.exe",
            "-NoLogo",
            "-NoProfile",
            "-Command",
            command
        );

        // Set directory context
        pb.directory(context.getCurrentDirectory().toFile());

        // inheritIO() streams stdout/stderr to the console directly
        // and connects stdin for interactive tools (like git commit, ping, npm login etc.)
        pb.inheritIO();

        long startTime = System.currentTimeMillis();
        int exitCode = -1;

        try {
            Process process = pb.start();
            activeProcess.set(process);

            exitCode = process.waitFor();
        } catch (IOException e) {
            System.err.println("Failed to start process: " + e.getMessage());
            exitCode = -1;
        } catch (InterruptedException e) {
            System.err.println("\nCommand execution interrupted.");
            Process process = activeProcess.get();
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
            Thread.currentThread().interrupt(); // Restore interrupted status
            exitCode = -1;
        } finally {
            activeProcess.set(null);
        }

        long durationMs = System.currentTimeMillis() - startTime;
        return new ExecutionResult(command, exitCode, durationMs);
    }

    public void interrupt() {
        Process process = activeProcess.get();
        if (process != null && process.isAlive()) {
            System.out.println("\n^C (Terminating subprocess...)");
            process.destroyForcibly();
        }
    }

    public boolean hasActiveProcess() {
        Process process = activeProcess.get();
        return process != null && process.isAlive();
    }
}
