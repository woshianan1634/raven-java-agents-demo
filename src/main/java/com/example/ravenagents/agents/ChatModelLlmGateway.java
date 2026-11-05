package com.example.ravenagents.agents;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

/**
 * LangChain4j-backed gateway used when OPENAI_API_KEY (and optional base URL) is set.
 */
public class ChatModelLlmGateway implements LlmGateway {

    private final ChatModel chatModel;

    public ChatModelLlmGateway(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        ChatResponse response = chatModel.chat(
                SystemMessage.from(systemPrompt == null ? "You are a helpful analysis agent." : systemPrompt),
                UserMessage.from(userPrompt == null ? "" : userPrompt)
        );
        return response.aiMessage().text();
    }

    @Override
    public String mode() {
        return "llm";
    }
}
