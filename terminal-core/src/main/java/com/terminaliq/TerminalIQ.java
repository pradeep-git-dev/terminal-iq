package com.terminaliq;

import com.terminaliq.context.ShellContext;
import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.router.CommandRouter;
import com.terminaliq.cli.InteractiveShell;
import com.terminaliq.history.DatabaseManager;
import com.terminaliq.history.HistoryRepository;

public class TerminalIQ {

    public static void main(String[] args) {
        // 1. Initialize Context
        ShellContext context = new ShellContext();

        // 2. Initialize Database and History Repository
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initialize();
        HistoryRepository historyRepository = new HistoryRepository(dbManager);

        // 3. Initialize native executor with stateful directory tracking
        CommandExecutor executor = new CommandExecutor(context);

        // 4. Initialize Router for command routing
        CommandRouter router = new CommandRouter(context, executor, historyRepository);

        // 5. Initialize and start the interactive terminal session
        InteractiveShell shell = new InteractiveShell(context, executor, router);
        shell.start();
    }
}
