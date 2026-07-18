package com.terminaliq.commands;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandCommandHandler {
    private final CommandRegistry registry;
    private Terminal terminal;
    private LineReader reader;

    public CommandCommandHandler(CommandRegistry registry) {
        this.registry = registry;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void setLineReader(LineReader reader) {
        this.reader = reader;
    }

    public void handle(String rawInput) {
        String trimmed = rawInput.trim();
        List<String> parts = CustomCommandEngine.parseArguments(trimmed);
        if (parts.size() < 2) {
            System.out.println("Usage: command [create|list|show|delete] [arguments]");
            return;
        }

        String subcommand = parts.get(1).toLowerCase();
        switch (subcommand) {
            case "create":
                executeCreate();
                break;
            case "list":
                executeList();
                break;
            case "show":
                if (parts.size() < 3) {
                    System.out.println("Usage: command show <command-name>");
                } else {
                    executeShow(parts.get(2));
                }
                break;
            case "delete":
                if (parts.size() < 3) {
                    System.out.println("Usage: command delete <command-name>");
                } else {
                    executeDelete(parts.get(2));
                }
                break;
            default:
                System.out.println("Unknown subcommand: " + subcommand);
                System.out.println("Usage: command [create|list|show|delete] [arguments]");
        }
    }

    private void executeCreate() {
        if (reader == null) {
            System.err.println("Error: Interactive line reader not available.");
            return;
        }

        try {
            System.out.println("Command name:");
            String name = reader.readLine().trim();
            if (name.isEmpty()) {
                System.out.println("Command name cannot be empty. Aborted.");
                return;
            }

            if (registry.findByName(name) != null) {
                System.out.println("Error: Command '" + name + "' already exists.");
                return;
            }

            System.out.println("\nDescription:");
            String description = reader.readLine().trim();

            System.out.println("\nArguments (comma-separated, optional):");
            String argsInput = reader.readLine().trim();
            List<String> arguments = new ArrayList<>();
            if (!argsInput.isEmpty()) {
                String[] argArray = argsInput.split(",");
                for (String arg : argArray) {
                    String cleaned = arg.trim();
                    if (!cleaned.isEmpty()) {
                        arguments.add(cleaned);
                    }
                }
            }

            List<String> steps = new ArrayList<>();
            int stepNum = 1;
            while (true) {
                System.out.println("\nAdd step " + stepNum + ":");
                String step = reader.readLine().trim();
                if (step.isEmpty()) {
                    break;
                }
                steps.add(step);
                stepNum++;
            }

            if (steps.isEmpty()) {
                System.out.println("Command must have at least one step. Aborted.");
                return;
            }

            CustomCommand newCommand = new CustomCommand(name, description, steps, arguments);
            boolean registered = registry.register(newCommand);
            if (registered) {
                System.out.println("\nCommand '" + name + "' created successfully.");
            } else {
                System.out.println("\nError: Failed to register command '" + name + "'. Name might have been taken.");
            }

        } catch (UserInterruptException | EndOfFileException e) {
            System.out.println("\nCommand creation aborted.");
        } catch (IOException e) {
            System.err.println("Error saving command: " + e.getMessage());
        }
    }

    private void executeList() {
        List<CustomCommand> commands = registry.list();
        System.out.printf("%-10s %s%n", "NAME", "DESCRIPTION");
        for (CustomCommand cmd : commands) {
            System.out.printf("%-10s %s%n", cmd.getName(), cmd.getDescription());
        }
    }

    private void executeShow(String name) {
        CustomCommand cmd = registry.findByName(name);
        if (cmd == null) {
            System.out.println("Command '" + name + "' not found.");
            return;
        }

        System.out.println("Name : " + cmd.getName());
        System.out.println();
        System.out.println("Steps:");
        List<String> steps = cmd.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            System.out.println((i + 1) + ". " + steps.get(i));
        }
    }

    private void executeDelete(String name) {
        CustomCommand cmd = registry.findByName(name);
        if (cmd == null) {
            System.out.println("Command '" + name + "' not found.");
            return;
        }

        if (reader == null) {
            System.err.println("Error: Interactive line reader not available.");
            return;
        }

        try {
            String prompt = "Delete command '" + name + "'? (y/N) ";
            String confirmation = reader.readLine(prompt).trim();
            if (confirmation.equalsIgnoreCase("y") || confirmation.equalsIgnoreCase("yes")) {
                boolean removed = registry.remove(name);
                if (removed) {
                    System.out.println("Command '" + name + "' deleted permanently.");
                } else {
                    System.out.println("Error: Failed to delete command '" + name + "'.");
                }
            } else {
                System.out.println("Delete aborted.");
            }
        } catch (UserInterruptException | EndOfFileException e) {
            System.out.println("\nDelete aborted.");
        } catch (IOException e) {
            System.err.println("Error deleting command: " + e.getMessage());
        }
    }
}
