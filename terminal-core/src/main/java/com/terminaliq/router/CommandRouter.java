package com.terminaliq.router;

import com.terminaliq.context.ShellContext;
import com.terminaliq.execution.CommandExecutor;
import java.io.IOException;

public class CommandRouter {

    private final ShellContext context;
    private final CommandExecutor executor;
    private boolean running = true;

    public CommandRouter(ShellContext context, CommandExecutor executor) {
        this.context = context;
        this.executor = executor;
    }

    public void route(String input) {
        if (input == null) {
            return;
        }
        input = input.trim();
        if (input.isEmpty()) {
            return;
        }

        // Handle Windows directory shorthand like 'cd..' or 'cd..\foo'
        if (input.equalsIgnoreCase("cd..")) {
            input = "cd ..";
        } else if (input.toLowerCase().startsWith("cd..")) {
            input = "cd .." + input.substring(4);
        }

        // Handle exit
        if (input.equalsIgnoreCase("exit")) {
            this.running = false;
            return;
        }

        // Handle cd
        if (input.equalsIgnoreCase("cd") || input.toLowerCase().startsWith("cd ") || input.toLowerCase().startsWith("cd\t")) {
            String targetPath = "";
            if (input.toLowerCase().startsWith("cd ") || input.toLowerCase().startsWith("cd\t")) {
                targetPath = input.substring(3).trim();
            }
            try {
                context.changeDirectory(targetPath);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return;
        }

        // Delegate other commands to native executor
        int exitCode = executor.execute(input);
        if (exitCode != 0) {
            // Diagnostic logging for non-zero exit codes if desired
            // System.out.println("[Process exited with code " + exitCode + "]");
        }
    }

    public boolean isRunning() {
        return running;
    }
}
