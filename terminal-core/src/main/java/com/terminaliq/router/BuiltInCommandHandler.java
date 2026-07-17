package com.terminaliq.router;

import com.terminaliq.context.ShellContext;
import com.terminaliq.context.ProjectContext;
import com.terminaliq.history.HistoryEntry;
import com.terminaliq.history.HistoryRepository;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.List;

public class BuiltInCommandHandler {

    private final ShellContext context;
    private final HistoryRepository historyRepository;
    private Terminal terminal;

    public BuiltInCommandHandler(ShellContext context, HistoryRepository historyRepository) {
        this.context = context;
        this.historyRepository = historyRepository;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void handle(String commandName, String rawInput) {
        if (commandName.equalsIgnoreCase("cd")) {
            executeCd(rawInput);
        } else if (commandName.equalsIgnoreCase("pwd")) {
            executePwd();
        } else if (commandName.equalsIgnoreCase("history")) {
            executeHistory(rawInput);
        } else if (commandName.equalsIgnoreCase("clear") || commandName.equalsIgnoreCase("cls")) {
            executeClear();
        } else if (commandName.equalsIgnoreCase("help")) {
            executeHelp();
        } else if (commandName.equalsIgnoreCase("context")) {
            executeContext();
        }
    }

    private void executeCd(String rawInput) {
        String targetPath = "";
        if (rawInput.toLowerCase().startsWith("cd ") || rawInput.toLowerCase().startsWith("cd\t")) {
            targetPath = rawInput.substring(3).trim();
        }
        try {
            context.changeDirectory(targetPath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void executePwd() {
        System.out.println(context.getCurrentDirectory());
    }

    private void executeContext() {
        ProjectContext projectCtx = context.getProjectContext();
        if (projectCtx == null) {
            System.out.println("No context available.");
            return;
        }

        System.out.println("Project Name : " + projectCtx.getProjectName());
        System.out.println();
        System.out.println("Type         : " + projectCtx.getProjectType());
        System.out.println();
        System.out.println("Git          : " + (projectCtx.isGitRepository() ? "Yes" : "No"));
        System.out.println("Docker       : " + (projectCtx.isDockerProject() ? "Yes" : "No"));
        System.out.println("Node         : " + (projectCtx.isNodeProject() ? "Yes" : "No"));
        System.out.println("Python       : " + (projectCtx.isPythonProject() ? "Yes" : "No"));
        System.out.println();
        System.out.println("Current Path :");
        System.out.println(projectCtx.getCurrentDirectory());
    }

    private void executeClear() {
        if (terminal != null) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.flush();
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private void executeHelp() {
        System.out.println("Terminal-IQ Commands");
        System.out.println();
        System.out.println("cd <path>       Change directory");
        System.out.println("pwd             Show current directory");
        System.out.println("history [limit] Show command history");
        System.out.println("clear           Clear terminal");
        System.out.println("context         Show project context");
        System.out.println("help            Show available commands");
        System.out.println("exit            Exit Terminal-IQ");
    }

    private void executeHistory(String rawInput) {
        int limit = -1;
        String[] parts = rawInput.trim().split("\\s+");
        if (parts.length > 1) {
            try {
                limit = Integer.parseInt(parts[1]);
                if (limit < 0) {
                    System.err.println("history: limit must be a positive integer.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("history: invalid limit format. Usage: history [limit]");
                return;
            }
        }

        List<HistoryEntry> entries = historyRepository.getHistory(limit);
        System.out.printf("%-5s%-24s%-7s%s%n", "ID", "COMMAND", "EXIT", "DURATION");
        for (HistoryEntry entry : entries) {
            System.out.printf("%-5d%-24s%-7d%dms%n",
                    entry.getId(),
                    entry.getCommand(),
                    entry.getExitCode(),
                    entry.getDurationMs()
            );
        }
    }
}
