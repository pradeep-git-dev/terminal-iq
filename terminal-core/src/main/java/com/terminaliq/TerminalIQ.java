package com.terminaliq;

import com.terminaliq.commands.CommandCommandHandler;
import com.terminaliq.commands.CommandRegistry;
import com.terminaliq.commands.CommandStorage;
import com.terminaliq.commands.CustomCommandEngine;
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

        // Initialize Command Storage, Registry, Engine and Handler
        CommandStorage commandStorage = new CommandStorage();
        CommandRegistry commandRegistry = new CommandRegistry(commandStorage);
        try {
            commandRegistry.loadFromStorage();
        } catch (Exception e) {
            System.err.println("Warning: Failed to load custom commands: " + e.getMessage());
        }
        CustomCommandEngine customCommandEngine = new CustomCommandEngine(executor);
        CommandCommandHandler commandCommandHandler = new CommandCommandHandler(commandRegistry);

        // 4. Initialize Router for command routing
        CommandRouter router = new CommandRouter(context, executor, historyRepository, commandRegistry, customCommandEngine, commandCommandHandler);

        // 5. Initialize and start the interactive terminal session
        InteractiveShell shell = new InteractiveShell(context, executor, router);
        shell.start();
    }
}
