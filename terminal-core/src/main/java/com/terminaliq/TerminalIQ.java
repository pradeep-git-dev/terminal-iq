package com.terminaliq;

import com.terminaliq.context.ShellContext;
import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.router.CommandRouter;
import com.terminaliq.cli.InteractiveShell;

public class TerminalIQ {

    public static void main(String[] args) {
        // 1. Initialize Context
        ShellContext context = new ShellContext();

        // 2. Initialize native executor with stateful directory tracking
        CommandExecutor executor = new CommandExecutor(context);

        // 3. Initialize Router for command routing
        CommandRouter router = new CommandRouter(context, executor);

        // 4. Initialize and start the interactive terminal session
        InteractiveShell shell = new InteractiveShell(context, executor, router);
        shell.start();
    }
}
