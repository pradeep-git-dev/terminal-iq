package com.terminaliq.intelligence;

public class AIRequest {
    private String requestId;
    private String query;
    private String shell;
    private String os;
    private String projectType;
    private String currentDirectory;

    public AIRequest() {}

    public AIRequest(String requestId, String query, String shell, String os, String projectType, String currentDirectory) {
        this.requestId = requestId;
        this.query = query;
        this.shell = shell;
        this.os = os;
        this.projectType = projectType;
        this.currentDirectory = currentDirectory;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getShell() {
        return shell;
    }

    public void setShell(String shell) {
        this.shell = shell;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
}
