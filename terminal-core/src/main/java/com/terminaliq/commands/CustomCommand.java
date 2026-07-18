package com.terminaliq.commands;

import java.util.ArrayList;
import java.util.List;

public class CustomCommand {
    private String name;
    private String description;
    private List<String> steps;
    private List<String> arguments;

    public CustomCommand() {
        this.steps = new ArrayList<>();
        this.arguments = new ArrayList<>();
    }

    public CustomCommand(String name, String description, List<String> steps, List<String> arguments) {
        this.name = name;
        this.description = description;
        this.steps = steps != null ? steps : new ArrayList<>();
        this.arguments = arguments != null ? arguments : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
