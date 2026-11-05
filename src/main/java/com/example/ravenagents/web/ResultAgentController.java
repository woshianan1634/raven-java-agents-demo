package com.example.ravenagents.web;

import com.example.ravenagents.agents.ResultAnalysisAgent;
import com.example.ravenagents.model.AnalyzeResponse;
import com.example.ravenagents.model.ResultAnalyzeRequest;
import com.example.ravenagents.model.openai.ChatCompletionRequest;
import com.example.ravenagents.model.openai.ChatCompletionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ResultAgentController {

    private final ResultAnalysisAgent agent;

    public ResultAgentController(ResultAnalysisAgent agent) {
        this.agent = agent;
    }

    @PostMapping("/api/agents/result/analyze")
    public AnalyzeResponse analyze(@RequestBody(required = false) ResultAnalyzeRequest request) {
        return agent.analyze(request);
    }

    @PostMapping("/agents/result/v1/chat/completions")
    public ChatCompletionResponse chatCompletions(@RequestBody ChatCompletionRequest request) {
        if (request != null && Boolean.TRUE.equals(request.getStream())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stream=true is not supported");
        }
        return agent.chat(request);
    }
}
