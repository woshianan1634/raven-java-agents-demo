package com.example.ravenagents.web;

import com.example.ravenagents.agents.TextAnalysisAgent;
import com.example.ravenagents.model.AnalyzeResponse;
import com.example.ravenagents.model.TextAnalyzeRequest;
import com.example.ravenagents.model.openai.ChatCompletionRequest;
import com.example.ravenagents.model.openai.ChatCompletionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TextAgentController {

    private final TextAnalysisAgent agent;

    public TextAgentController(TextAnalysisAgent agent) {
        this.agent = agent;
    }

    @PostMapping("/api/agents/text/analyze")
    public AnalyzeResponse analyze(@RequestBody(required = false) TextAnalyzeRequest request) {
        return agent.analyze(request);
    }

    @PostMapping("/agents/text/v1/chat/completions")
    public ChatCompletionResponse chatCompletions(@RequestBody ChatCompletionRequest request) {
        if (request != null && Boolean.TRUE.equals(request.getStream())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stream=true is not supported");
        }
        return agent.chat(request);
    }
}
