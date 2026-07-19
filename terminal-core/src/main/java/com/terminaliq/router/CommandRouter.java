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
import com.terminaliq.intelligence.AIRequest;
import com.terminaliq.intelligence.AIResponse;
import com.terminaliq.intelligence.IntelligenceClient;
import com.terminaliq.safety.SafetyEngine;
import com.terminaliq.safety.SafetyEngine.SafetyResult;
import java.util.UUID;

public class CommandRouter {

    private final ShellContext context;
    private final CommandExecutor executor;
    private final BuiltInCommandHandler builtInHandler;
    private final HistoryRepository historyRepository;
    private final CommandRegistry commandRegistry;
    private final CustomCommandEngine customCommandEngine;
    private final CommandCommandHandler commandCommandHandler;
    private final IntelligenceClient intelligenceClient;
    private final SafetyEngine safetyEngine;
    private LineReader lineReader;
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

        String serviceUrl = System.getenv("INTELLIGENCE_SERVICE_URL");
        if (serviceUrl == null || serviceUrl.trim().isEmpty()) {
            serviceUrl = "http://localhost:8000";
        }
        this.intelligenceClient = new IntelligenceClient(serviceUrl);
        this.safetyEngine = new SafetyEngine();
    }

    public void setTerminal(Terminal terminal) {
        this.builtInHandler.setTerminal(terminal);
        this.commandCommandHandler.setTerminal(terminal);
    }

    public void setLineReader(LineReader reader) {
        this.lineReader = reader;
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

        // Check if it is an AI Query prefix
        boolean isAiQuery = false;
        String aiQueryText = "";
        String lowerInput = resolvedInput.toLowerCase();
        if (lowerInput.startsWith("ai ") && resolvedInput.length() > 3) {
            isAiQuery = true;
            aiQueryText = resolvedInput.substring(3).trim();
        } else if (lowerInput.startsWith("ai:") && resolvedInput.length() > 3) {
            isAiQuery = true;
            aiQueryText = resolvedInput.substring(3).trim();
        } else if (lowerInput.startsWith("/ai ") && resolvedInput.length() > 4) {
            isAiQuery = true;
            aiQueryText = resolvedInput.substring(4).trim();
        } else if (lowerInput.startsWith("/ai") && resolvedInput.length() > 3) {
            isAiQuery = true;
            aiQueryText = resolvedInput.substring(3).trim();
        } else if (lowerInput.equals("ai") || lowerInput.equals("ai:") || lowerInput.equals("/ai")) {
            System.out.println("Usage: ai <natural language query>");
            return;
        }

        if (isAiQuery) {
            handleAiQuery(aiQueryText);
            return;
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

    private void handleAiQuery(String query) {
        String requestId = UUID.randomUUID().toString();
        String shell = "powershell";
        String os = "windows";
        
        com.terminaliq.context.ProjectContext projectCtx = context.getProjectContext();
        String projectType = projectCtx != null ? projectCtx.getProjectType().toString() : "UNKNOWN";
        String currentDirectory = context.getCurrentDirectory().toString();

        AIRequest request = new AIRequest(requestId, query, shell, os, projectType, currentDirectory);

        try {
            System.out.println("Querying AI Service...");
            AIResponse response = intelligenceClient.generate(request);

            if (response.getCommand() == null || response.getCommand().trim().isEmpty()) {
                System.out.println("\nAI could not confidently generate a command.");
                if (response.getExplanation() != null && !response.getExplanation().isEmpty()) {
                    System.out.println("Reason: " + response.getExplanation());
                } else {
                    System.out.println("Please rephrase your request.");
                }
                return;
            }

            String suggestedCommand = response.getCommand();

            // Safety Engine check
            SafetyResult safetyResult = safetyEngine.check(suggestedCommand);
            
            if (safetyResult.getLevel() == SafetyEngine.Level.BLOCKED) {
                System.out.println("\n[SAFETY BLOCK] The suggested command is blocked due to safety policies.");
                System.out.println("Command: " + suggestedCommand);
                System.out.println("Reason: " + safetyResult.getReason());
                return;
            }

            if (safetyResult.getLevel() == SafetyEngine.Level.WARNING) {
                System.out.println("\n[WARNING] This command is flagged as DANGEROUS!");
                System.out.println("Reason: " + safetyResult.getReason());
            }

            System.out.println("\nSuggested Command:");
            System.out.println(suggestedCommand);
            System.out.println("\nExplanation:");
            System.out.println(response.getExplanation());
            
            if (response.getReason() != null && !response.getReason().trim().isEmpty()) {
                System.out.println("\nSafety Notes:");
                System.out.println(response.getReason());
            }
            
            System.out.println("\n[Request ID: " + requestId + " | Prompt Version: " + response.getPromptVersion() + "]");
            System.out.println();

            boolean confirmed = promptConfirmation("Execute? (Y/n) ");
            if (confirmed) {
                System.out.println("Executing command...\n");
                ExecutionResult result = executor.execute(suggestedCommand);
                HistoryEntry entry = new HistoryEntry(
                    suggestedCommand,
                    context.getCurrentDirectory().toString(),
                    result.getExitCode(),
                    result.getDurationMs()
                );
                historyRepository.save(entry);
            } else {
                System.out.println("Execution aborted.");
            }

        } catch (Exception e) {
            System.out.println("\nAI could not confidently generate a command.");
            System.out.println("Please rephrase your request.");
        }
    }

    private boolean promptConfirmation(String promptText) {
        if (lineReader != null) {
            try {
                String line = lineReader.readLine(promptText).trim();
                return line.isEmpty() || line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes");
            } catch (org.jline.reader.UserInterruptException | org.jline.reader.EndOfFileException e) {
                System.out.println("\nExecution cancelled.");
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        System.out.print(promptText);
        System.out.flush();
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            String line = br.readLine();
            if (line == null) {
                return false;
            }
            line = line.trim();
            return line.isEmpty() || line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
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
