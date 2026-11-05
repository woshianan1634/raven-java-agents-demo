package com.example.ravenagents.agents;

import com.example.ravenagents.model.AnalyzeResponse;
import com.example.ravenagents.model.TextAnalyzeRequest;
import com.example.ravenagents.model.openai.ChatCompletionRequest;
import com.example.ravenagents.model.openai.ChatCompletionResponse;
import com.example.ravenagents.model.openai.ChatMessage;

/**
 * Specialty agent: 文字分析 (text analysis).
 */
public class TextAnalysisAgent {

    public static final String ID = "text-agent";
    public static final String DESCRIPTION =
            "文字分析 Agent：对输入文本做摘要、关键实体、情感/语气与潜在问题提炼。";
    public static final String SYSTEM_PROMPT =
            "你是 TextAnalysisAgent（文字分析）。对用户文本做结构化分析："
                    + "摘要、关键实体、情感/语气、潜在风险或待核实点。用简洁中文回答。";

    private final LlmGateway llmGateway;

    public TextAnalysisAgent(LlmGateway llmGateway) {
        this.llmGateway = llmGateway;
    }

    public AnalyzeResponse analyze(TextAnalyzeRequest request) {
        TextAnalyzeRequest req = request == null ? new TextAnalyzeRequest() : request;
        String content = req.getContent() == null ? "" : req.getContent();
        String userPrompt = "请分析以下文本：\n" + content;
        String analysis = llmGateway.complete(SYSTEM_PROMPT, userPrompt);
        return new AnalyzeResponse(ID, llmGateway.mode(), analysis);
    }

    public ChatCompletionResponse chat(ChatCompletionRequest request) {
        String model = request == null ? null : request.getModel();
        String userPrompt = extractUserContent(request);
        if (userPrompt == null || userPrompt.isBlank()) {
            userPrompt = "请对给定文本做文字分析。";
        }
        String analysis = llmGateway.complete(SYSTEM_PROMPT, userPrompt);
        return ChatCompletionResponse.of(model == null || model.isBlank() ? ID : model, analysis);
    }

    private static String extractUserContent(ChatCompletionRequest request) {
        if (request == null || request.getMessages() == null) {
            return "";
        }
        StringBuilder user = new StringBuilder();
        for (ChatMessage msg : request.getMessages()) {
            if (msg == null || msg.getContent() == null) {
                continue;
            }
            String role = msg.getRole() == null ? "" : msg.getRole().toLowerCase();
            if ("system".equals(role)) {
                continue;
            }
            if (!user.isEmpty()) {
                user.append('\n');
            }
            user.append(msg.getContent());
        }
        return user.toString();
    }
}
