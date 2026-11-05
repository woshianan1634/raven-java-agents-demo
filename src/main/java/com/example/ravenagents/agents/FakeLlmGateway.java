package com.example.ravenagents.agents;

/**
 * Deterministic offline responder so {@code mvn test} and demos run without API keys.
 */
public class FakeLlmGateway implements LlmGateway {

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        String snippet = userPrompt == null ? "" : userPrompt.replaceAll("\\s+", " ").trim();
        if (snippet.length() > 240) {
            snippet = snippet.substring(0, 240) + "...";
        }
        String roleHint = systemPrompt == null ? "agent" : systemPrompt.replaceAll("\\s+", " ").trim();
        if (roleHint.length() > 80) {
            roleHint = roleHint.substring(0, 80) + "...";
        }
        return "[fake] role=" + roleHint + " | input=" + snippet;
    }

    @Override
    public String mode() {
        return "fake";
    }
}
