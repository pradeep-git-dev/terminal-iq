package com.terminaliq.intelligence;

public class AIResponse {
    private String command;
    private String explanation;
    private boolean needsConfirmation;
    private String reason;
    private String promptVersion;

    public AIResponse() {}

    public AIResponse(String command, String explanation, boolean needsConfirmation, String reason, String promptVersion) {
        this.command = command;
        this.explanation = explanation;
        this.needsConfirmation = needsConfirmation;
        this.reason = reason;
        this.promptVersion = promptVersion;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isNeedsConfirmation() {
        return needsConfirmation;
    }

    public void setNeedsConfirmation(boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }
}
