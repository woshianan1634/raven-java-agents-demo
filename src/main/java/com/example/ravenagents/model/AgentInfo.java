package com.example.ravenagents.model;

public class AgentInfo {

    private String id;
    private String name;
    private String description;
    private String domainPath;
    private String openAiPath;
    private String baseUrlHint;

    public AgentInfo() {
    }

    public AgentInfo(String id, String name, String description,
                     String domainPath, String openAiPath, String baseUrlHint) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.domainPath = domainPath;
        this.openAiPath = openAiPath;
        this.baseUrlHint = baseUrlHint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDomainPath() {
        return domainPath;
    }

    public void setDomainPath(String domainPath) {
        this.domainPath = domainPath;
    }

    public String getOpenAiPath() {
        return openAiPath;
    }

    public void setOpenAiPath(String openAiPath) {
        this.openAiPath = openAiPath;
    }

    public String getBaseUrlHint() {
        return baseUrlHint;
    }

    public void setBaseUrlHint(String baseUrlHint) {
        this.baseUrlHint = baseUrlHint;
    }
}
