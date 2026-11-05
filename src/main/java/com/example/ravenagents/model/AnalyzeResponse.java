package com.example.ravenagents.model;

public class AnalyzeResponse {

    private String agentId;
    private String mode;
    private String analysis;

    public AnalyzeResponse() {
    }

    public AnalyzeResponse(String agentId, String mode, String analysis) {
        this.agentId = agentId;
        this.mode = mode;
        this.analysis = analysis;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }
}
