package com.example.ravenagents.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultAnalyzeRequest {

    private String imageFindings;
    private String textFindings;
    /** Optional extra instruction for synthesis. */
    private String instruction;

    public String getImageFindings() {
        return imageFindings;
    }

    public void setImageFindings(String imageFindings) {
        this.imageFindings = imageFindings;
    }

    public String getTextFindings() {
        return textFindings;
    }

    public void setTextFindings(String textFindings) {
        this.textFindings = textFindings;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
