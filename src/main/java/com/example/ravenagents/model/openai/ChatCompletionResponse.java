package com.example.ravenagents.model.openai;

import java.util.List;

public class ChatCompletionResponse {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatCompletionChoice> choices;
    private Usage usage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatCompletionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<ChatCompletionChoice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public static ChatCompletionResponse of(String model, String content) {
        ChatCompletionResponse response = new ChatCompletionResponse();
        response.setId("chatcmpl-" + Long.toHexString(System.nanoTime()));
        response.setObject("chat.completion");
        response.setCreated(System.currentTimeMillis() / 1000L);
        response.setModel(model == null || model.isBlank() ? "raven-java-agent" : model);
        response.setChoices(List.of(
                new ChatCompletionChoice(0, new ChatMessage("assistant", content), "stop")
        ));
        int approx = content == null ? 0 : Math.max(1, content.length() / 4);
        response.setUsage(new Usage(0, approx, approx));
        return response;
    }
}
