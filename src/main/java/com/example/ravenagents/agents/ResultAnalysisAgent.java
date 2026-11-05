package com.example.ravenagents.agents;

import com.example.ravenagents.model.AnalyzeResponse;
import com.example.ravenagents.model.ResultAnalyzeRequest;
import com.example.ravenagents.model.openai.ChatCompletionRequest;
import com.example.ravenagents.model.openai.ChatCompletionResponse;
import com.example.ravenagents.model.openai.ChatMessage;

/**
 * Specialty agent: 结果分析 (synthesizes upstream image + text findings).
 */
public class ResultAnalysisAgent {

    public static final String ID = "result-agent";
    public static final String DESCRIPTION =
            "结果分析 Agent：综合上游图像分析与文字分析结论，给出一致性判断、冲突点与最终汇总。";
    public static final String SYSTEM_PROMPT =
            "你是 ResultAnalysisAgent（结果分析）。综合上游的图像分析与文字分析结果，"
                    + "给出一致性判断、冲突点、关键结论与可执行建议。用简洁中文回答。";

    private final LlmGateway llmGateway;

    public ResultAnalysisAgent(LlmGateway llmGateway) {
        this.llmGateway = llmGateway;
    }

    public AnalyzeResponse analyze(ResultAnalyzeRequest request) {
        ResultAnalyzeRequest req = request == null ? new ResultAnalyzeRequest() : request;
        String userPrompt = buildDomainPrompt(req);
        String analysis = llmGateway.complete(SYSTEM_PROMPT, userPrompt);
        return new AnalyzeResponse(ID, llmGateway.mode(), analysis);
    }

    public ChatCompletionResponse chat(ChatCompletionRequest request) {
        String model = request == null ? null : request.getModel();
        String userPrompt = extractUserContent(request);
        if (userPrompt == null || userPrompt.isBlank()) {
            userPrompt = "请综合上游图像与文字分析做结果汇总。";
        }
        String analysis = llmGateway.complete(SYSTEM_PROMPT, userPrompt);
        return ChatCompletionResponse.of(model == null || model.isBlank() ? ID : model, analysis);
    }

    private static String buildDomainPrompt(ResultAnalyzeRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("请综合以下上游分析结果。\n");
        sb.append("imageFindings:\n")
                .append(req.getImageFindings() == null ? "(empty)" : req.getImageFindings())
                .append('\n');
        sb.append("textFindings:\n")
                .append(req.getTextFindings() == null ? "(empty)" : req.getTextFindings())
                .append('\n');
        if (req.getInstruction() != null && !req.getInstruction().isBlank()) {
            sb.append("额外指令: ").append(req.getInstruction().trim()).append('\n');
        }
        return sb.toString();
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
