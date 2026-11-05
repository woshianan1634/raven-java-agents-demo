package com.example.ravenagents.agents;

/**
 * Thin abstraction over chat completion so agents can run with either a real
 * LangChain4j ChatModel or a deterministic fake for offline tests.
 */
public interface LlmGateway {

    /**
     * @return analysis text
     */
    String complete(String systemPrompt, String userPrompt);

    /**
     * @return "llm" when backed by a real model, "fake" otherwise
     */
    String mode();
}
