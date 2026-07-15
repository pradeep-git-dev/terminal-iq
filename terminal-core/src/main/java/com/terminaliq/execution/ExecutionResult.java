package com.terminaliq.execution;

public class ExecutionResult {
    private final String command;
    private final int exitCode;
    private final long durationMs;

    public ExecutionResult(String command, int exitCode, long durationMs) {
        this.command = command;
        this.exitCode = exitCode;
        this.durationMs = durationMs;
    }

    public String getCommand() {
        return command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public boolean isSuccessful() {
        return exitCode == 0;
    }
}
