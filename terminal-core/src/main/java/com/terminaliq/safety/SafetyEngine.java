package com.terminaliq.safety;

import java.util.regex.Pattern;

public class SafetyEngine {

    public enum Level {
        SAFE,
        WARNING,
        BLOCKED
    }

    public static class SafetyResult {
        private final Level level;
        private final String reason;

        public SafetyResult(Level level, String reason) {
            this.level = level;
            this.reason = reason;
        }

        public Level getLevel() {
            return level;
        }

        public String getReason() {
            return reason;
        }
    }

    // Rules for BLOCKED commands
    private static final Pattern FORMAT_PATTERN = Pattern.compile("(?i)\\b(format-volume|clear-disk|initialize-disk|remove-partition)\\b");
    private static final Pattern SYSTEM_DEL_PATTERN = Pattern.compile("(?i)\\b(remove-item|del|rm)\\b.*(c:\\\\windows|c:/windows|\\\\system32)");

    // Rules for WARNING commands
    private static final Pattern DESTRUCTIVE_DEL_PATTERN = Pattern.compile("(?i)\\b(remove-item|del|rm)\\b.*\\b(recurse|force|\\-rf|\\/s|\\/f)\\b");
    private static final Pattern FORCE_PUSH_PATTERN = Pattern.compile("(?i)\\bgit\\b.*\\bpush\\b.*\\b(force|\\-f)\\b");
    private static final Pattern DOCKER_PRUNE_PATTERN = Pattern.compile("(?i)\\bdocker\\b.*\\bprune\\b");

    public SafetyResult check(String command) {
        if (command == null || command.trim().isEmpty()) {
            return new SafetyResult(Level.SAFE, null);
        }

        String normalized = command.trim();

        // 1. Check Blocked rules first
        if (FORMAT_PATTERN.matcher(normalized).find()) {
            return new SafetyResult(Level.BLOCKED, "Disk partition initialization or volume formatting commands are strictly blocked.");
        }
        if (SYSTEM_DEL_PATTERN.matcher(normalized).find()) {
            return new SafetyResult(Level.BLOCKED, "Destructive file deletion inside system directories (e.g., Windows or System32) is strictly blocked.");
        }

        // 2. Check Warning rules
        if (DESTRUCTIVE_DEL_PATTERN.matcher(normalized).find()) {
            return new SafetyResult(Level.WARNING, "Recursive or forced file deletion command detected.");
        }
        if (FORCE_PUSH_PATTERN.matcher(normalized).find()) {
            return new SafetyResult(Level.WARNING, "Git force push command detected, which can overwrite remote history.");
        }
        if (DOCKER_PRUNE_PATTERN.matcher(normalized).find()) {
            return new SafetyResult(Level.WARNING, "Docker prune command detected, which removes unused docker data system-wide.");
        }

        return new SafetyResult(Level.SAFE, null);
    }
}
