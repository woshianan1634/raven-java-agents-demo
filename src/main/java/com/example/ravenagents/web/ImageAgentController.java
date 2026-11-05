package com.example.ravenagents.web;

import com.example.ravenagents.agents.ImageAnalysisAgent;
import com.example.ravenagents.model.AnalyzeResponse;
import com.example.ravenagents.model.ImageAnalyzeRequest;
import com.example.ravenagents.model.openai.ChatCompletionRequest;
import com.example.ravenagents.model.openai.ChatCompletionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ImageAgentController {

    private final ImageAnalysisAgent agent;

    public ImageAgentController(ImageAnalysisAgent agent) {
        this.agent = agent;
    }

    @PostMapping("/api/agents/image/analyze")
    public AnalyzeResponse analyze(@RequestBody(required = false) ImageAnalyzeRequest request) {
        return agent.analyze(request);
    }

    @PostMapping("/agents/image/v1/chat/completions")
    public ChatCompletionResponse chatCompletions(@RequestBody ChatCompletionRequest request) {
        if (request != null && Boolean.TRUE.equals(request.getStream())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stream=true is not supported");
        }
        return agent.chat(request);
    }
}
