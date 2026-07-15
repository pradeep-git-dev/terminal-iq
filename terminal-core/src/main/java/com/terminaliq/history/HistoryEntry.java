package com.terminaliq.history;

public class HistoryEntry {
    private final int id;
    private final String command;
    private final String workingDirectory;
    private final int exitCode;
    private final long durationMs;
    private final String executedAt;

    public HistoryEntry(int id, String command, String workingDirectory, int exitCode, long durationMs, String executedAt) {
        this.id = id;
        this.command = command;
        this.workingDirectory = workingDirectory;
        this.exitCode = exitCode;
        this.durationMs = durationMs;
        this.executedAt = executedAt;
    }

    public HistoryEntry(String command, String workingDirectory, int exitCode, long durationMs) {
        this.id = 0;
        this.command = command;
        this.workingDirectory = workingDirectory;
        this.exitCode = exitCode;
        this.durationMs = durationMs;
        this.executedAt = null;
    }

    public int getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public int getExitCode() {
        return exitCode;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getExecutedAt() {
        return executedAt;
    }
}
