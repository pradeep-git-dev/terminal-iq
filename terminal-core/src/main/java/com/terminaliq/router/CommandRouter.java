package com.terminaliq.router;

import com.terminaliq.context.ShellContext;
import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.execution.ExecutionResult;
import com.terminaliq.history.HistoryEntry;
import com.terminaliq.history.HistoryRepository;
import org.jline.terminal.Terminal;

public class CommandRouter {

    private final ShellContext context;
    private final CommandExecutor executor;
    private final BuiltInCommandHandler builtInHandler;
    private final HistoryRepository historyRepository;
    private boolean running = true;

    public CommandRouter(ShellContext context, CommandExecutor executor, HistoryRepository historyRepository) {
        this.context = context;
        this.executor = executor;
        this.historyRepository = historyRepository;
        this.builtInHandler = new BuiltInCommandHandler(context, historyRepository);
    }

    public void setTerminal(Terminal terminal) {
        this.builtInHandler.setTerminal(terminal);
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

        if (isBuiltIn(commandName)) {
            builtInHandler.handle(commandName, resolvedInput);
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
