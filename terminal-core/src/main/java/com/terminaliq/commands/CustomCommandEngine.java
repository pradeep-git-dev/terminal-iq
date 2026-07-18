package com.terminaliq.commands;

import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.execution.ExecutionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCommandEngine {
    private final CommandExecutor executor;

    public CustomCommandEngine(CommandExecutor executor) {
        this.executor = executor;
    }

    public ExecutionResult execute(CustomCommand command) {
        return execute(command, command.getName());
    }

    public ExecutionResult execute(CustomCommand command, String rawInput) {
        long startTime = System.currentTimeMillis();
        int totalSteps = command.getSteps().size();
        int lastExitCode = 0;

        List<String> parsedInput = parseArguments(rawInput);
        List<String> userArgs = new ArrayList<>();
        if (parsedInput.size() > 1) {
            userArgs = parsedInput.subList(1, parsedInput.size());
        }

        Map<String, String> argMap = new HashMap<>();
        List<String> definedArgs = command.getArguments();
        for (int i = 0; i < definedArgs.size(); i++) {
            String placeholder = definedArgs.get(i);
            String value = "";
            if (i < userArgs.size()) {
                value = userArgs.get(i);
            }
            argMap.put(placeholder, value);
        }

        for (int i = 0; i < totalSteps; i++) {
            String step = command.getSteps().get(i);
            int stepNum = i + 1;

            String resolvedStep = step;
            for (Map.Entry<String, String> entry : argMap.entrySet()) {
                resolvedStep = resolvedStep.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            System.out.println("[" + stepNum + "/" + totalSteps + "] " + resolvedStep);
            System.out.println("✔ Running...");

            ExecutionResult result = executor.execute(resolvedStep);
            if (result.getExitCode() == 0) {
                System.out.println("✔ Success");
                System.out.println();
            } else {
                System.out.println("Step " + stepNum + " failed.");
                System.out.println();
                System.out.println(resolvedStep);
                System.out.println();
                System.out.println("Exit Code: " + result.getExitCode());
                System.out.println();
                System.out.println("Workflow stopped.");
                lastExitCode = result.getExitCode();
                break;
            }
        }

        long durationMs = System.currentTimeMillis() - startTime;
        return new ExecutionResult(command.getName(), lastExitCode, durationMs);
    }

    public static List<String> parseArguments(String rawInput) {
        List<String> list = new ArrayList<>();
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return list;
        }
        boolean inDoubleQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rawInput.length(); i++) {
            char c = rawInput.charAt(i);
            if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
            } else if (Character.isWhitespace(c) && !inDoubleQuotes && !inSingleQuotes) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list;
    }
}
