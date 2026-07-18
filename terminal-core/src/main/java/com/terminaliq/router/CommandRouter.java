package com.terminaliq.router;

import com.terminaliq.commands.CommandCommandHandler;
import com.terminaliq.commands.CommandRegistry;
import com.terminaliq.commands.CustomCommand;
import com.terminaliq.commands.CustomCommandEngine;
import com.terminaliq.context.ShellContext;
import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.execution.ExecutionResult;
import com.terminaliq.history.HistoryEntry;
import com.terminaliq.history.HistoryRepository;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

public class CommandRouter {

    private final ShellContext context;
    private final CommandExecutor executor;
    private final BuiltInCommandHandler builtInHandler;
    private final HistoryRepository historyRepository;
    private final CommandRegistry commandRegistry;
    private final CustomCommandEngine customCommandEngine;
    private final CommandCommandHandler commandCommandHandler;
    private boolean running = true;

    public CommandRouter(ShellContext context, CommandExecutor executor, HistoryRepository historyRepository,
                         CommandRegistry commandRegistry, CustomCommandEngine customCommandEngine,
                         CommandCommandHandler commandCommandHandler) {
        this.context = context;
        this.executor = executor;
        this.historyRepository = historyRepository;
        this.commandRegistry = commandRegistry;
        this.customCommandEngine = customCommandEngine;
        this.commandCommandHandler = commandCommandHandler;
        this.builtInHandler = new BuiltInCommandHandler(context, historyRepository);
    }

    public void setTerminal(Terminal terminal) {
        this.builtInHandler.setTerminal(terminal);
        this.commandCommandHandler.setTerminal(terminal);
    }

    public void setLineReader(LineReader reader) {
        this.commandCommandHandler.setLineReader(reader);
    }

    public void route(String input) {
        if (input == null) {
            return;
        }
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return;
        }

        // Handle Windows directory shorthand like 'cd..' or 'cd..\foo'
        String resolvedInput = trimmedInput;
        if (resolvedInput.equalsIgnoreCase("cd..")) {
            resolvedInput = "cd ..";
        } else if (resolvedInput.toLowerCase().startsWith("cd..")) {
            resolvedInput = "cd .." + resolvedInput.substring(4);
        }

        String commandName = getCommandName(resolvedInput);

        if (commandName.equalsIgnoreCase("exit")) {
            this.running = false;
            return;
        }

        if (commandName.equalsIgnoreCase("command")) {
            commandCommandHandler.handle(resolvedInput);
        } else if (isBuiltIn(commandName)) {
            builtInHandler.handle(commandName, resolvedInput);
        } else {
            // Check if it's a Custom Command
            CustomCommand customCmd = commandRegistry.findByName(commandName);
            if (customCmd != null) {
                ExecutionResult result = customCommandEngine.execute(customCmd, resolvedInput);
                HistoryEntry entry = new HistoryEntry(
                    resolvedInput,
                    context.getCurrentDirectory().toString(),
                    result.getExitCode(),
                    result.getDurationMs()
                );
                historyRepository.save(entry);
            } else {
                // Native Command
                ExecutionResult result = executor.execute(resolvedInput);
                
                // Store native executed commands in history
                HistoryEntry entry = new HistoryEntry(
                    resolvedInput,
                    context.getCurrentDirectory().toString(),
                    result.getExitCode(),
                    result.getDurationMs()
                );
                historyRepository.save(entry);
            }
        }
    }

    private String getCommandName(String input) {
        int firstSpace = input.indexOf(' ');
        int firstTab = input.indexOf('\t');
        int splitIdx = -1;
        if (firstSpace != -1 && firstTab != -1) {
            splitIdx = Math.min(firstSpace, firstTab);
        } else if (firstSpace != -1) {
            splitIdx = firstSpace;
        } else if (firstTab != -1) {
            splitIdx = firstTab;
        }

        if (splitIdx == -1) {
            return input;
        }
        return input.substring(0, splitIdx);
    }

    private boolean isBuiltIn(String commandName) {
        return commandName.equalsIgnoreCase("cd")
                || commandName.equalsIgnoreCase("history")
                || commandName.equalsIgnoreCase("clear")
                || commandName.equalsIgnoreCase("cls")
                || commandName.equalsIgnoreCase("pwd")
                || commandName.equalsIgnoreCase("help")
                || commandName.equalsIgnoreCase("context");
    }

    public boolean isRunning() {
        return running;
    }
}
