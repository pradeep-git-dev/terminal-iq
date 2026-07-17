package com.terminaliq.cli;

import com.terminaliq.context.ShellContext;
import com.terminaliq.context.ProjectContext;
import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.router.CommandRouter;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class InteractiveShell {

    private final ShellContext context;
    private final CommandExecutor executor;
    private final CommandRouter router;
    private Terminal terminal;
    private LineReader reader;

    public InteractiveShell(ShellContext context, CommandExecutor executor, CommandRouter router) {
        this.context = context;
        this.executor = executor;
        this.router = router;
    }

    public void start() {
        try {
            // Build terminal with system capabilities (ANSI support, raw mode, signal hookup)
            this.terminal = TerminalBuilder.builder()
                .system(true)
                .build();

            // Set up signal handler for Ctrl+C (INT signal) when executing subprocesses
            this.terminal.handle(Terminal.Signal.INT, signal -> {
                if (executor.hasActiveProcess()) {
                    executor.interrupt();
                }
            });

            this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

            this.router.setTerminal(terminal);

        } catch (IOException e) {
            System.err.println("Failed to initialize JLine Terminal: " + e.getMessage());
            System.out.println("Falling back to standard Console...");
            return;
        }

        System.out.println("=================================================");
        System.out.println("         Terminal-IQ Core Terminal v0.1          ");
        System.out.println("=================================================");
        System.out.println("Type 'exit' or press Ctrl+D to exit shell.\n");

        while (router.isRunning()) {
            String line;
            try {
                String prompt = buildPrompt();
                line = reader.readLine(prompt);
            } catch (UserInterruptException e) {
                // Ctrl+C was pressed while reading input
                // Just clear current line and display a fresh prompt
                System.out.println();
                continue;
            } catch (EndOfFileException e) {
                // Ctrl+D was pressed (EOF)
                System.out.println("\nExiting Terminal-IQ...");
                break;
            }

            if (line == null) {
                break;
            }

            router.route(line);
        }

        try {
            terminal.close();
        } catch (Exception e) {
            // Ignore close errors
        }
    }

    private String buildPrompt() {
        String username = context.getUsername();
        String currentDir = context.getCurrentDirectory().toString();
        ProjectContext projectCtx = context.getProjectContext();
        String projectType = projectCtx != null ? projectCtx.getProjectType().toString() : "UNKNOWN";
        return username + "@terminal-iq [" + projectType + "] " + currentDir + "> ";
    }
}
