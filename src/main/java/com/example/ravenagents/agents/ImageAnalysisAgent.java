package com.example.ravenagents.agents;

import com.example.ravenagents.model.AnalyzeResponse;
import com.example.ravenagents.model.ImageAnalyzeRequest;
import com.example.ravenagents.model.openai.ChatCompletionRequest;
import com.example.ravenagents.model.openai.ChatCompletionResponse;
import com.example.ravenagents.model.openai.ChatMessage;

/**
 * Specialty agent: 图像分析 (image analysis).
 */
public class ImageAnalysisAgent {

    public static final String ID = "image-agent";
    public static final String DESCRIPTION =
            "图像分析 Agent：根据图片 URL / base64（可选）与文字描述，输出视觉内容、物体、场景与风险要点。";
    public static final String SYSTEM_PROMPT =
            "你是 ImageAnalysisAgent（图像分析）。根据用户提供的图片线索与描述，"
                    + "给出结构化的视觉分析：主体、场景、文字 OCR 线索、异常/风险。用简洁中文回答。";

    private final LlmGateway llmGateway;

    public ImageAnalysisAgent(LlmGateway llmGateway) {
        this.llmGateway = llmGateway;
    }

    public AnalyzeResponse analyze(ImageAnalyzeRequest request) {
        ImageAnalyzeRequest req = request == null ? new ImageAnalyzeRequest() : request;
        String userPrompt = buildDomainPrompt(req);
        String analysis = llmGateway.complete(SYSTEM_PROMPT, userPrompt);
        return new AnalyzeResponse(ID, llmGateway.mode(), analysis);
    }

    public ChatCompletionResponse chat(ChatCompletionRequest request) {
        String model = request == null ? null : request.getModel();
        String userPrompt = extractUserContent(request);
        if (userPrompt == null || userPrompt.isBlank()) {
            userPrompt = "请对给定图片线索做图像分析。";
        }
        String analysis = llmGateway.complete(SYSTEM_PROMPT, userPrompt);
        return ChatCompletionResponse.of(model == null || model.isBlank() ? ID : model, analysis);
    }

    private static String buildDomainPrompt(ImageAnalyzeRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("请分析以下图像任务。\n");
        if (req.getDescription() != null && !req.getDescription().isBlank()) {
            sb.append("描述/问题: ").append(req.getDescription().trim()).append('\n');
        }
        if (req.getImageUrl() != null && !req.getImageUrl().isBlank()) {
            sb.append("imageUrl: ").append(req.getImageUrl().trim()).append('\n');
        }
        if (req.getImageBase64() != null && !req.getImageBase64().isBlank()) {
            int len = req.getImageBase64().length();
            sb.append("imageBase64: <present, length=").append(len).append(">\n");
        }
        if ((req.getImageUrl() == null || req.getImageUrl().isBlank())
                && (req.getImageBase64() == null || req.getImageBase64().isBlank())
                && (req.getDescription() == null || req.getDescription().isBlank())) {
            sb.append("(未提供图片或描述，请返回通用占位分析说明)\n");
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
