package com.example.ravenagents.web;

import com.example.ravenagents.agents.ImageAnalysisAgent;
import com.example.ravenagents.agents.ResultAnalysisAgent;
import com.example.ravenagents.agents.TextAnalysisAgent;
import com.example.ravenagents.model.AgentInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentListController {

    @GetMapping
    public Map<String, Object> list() {
        List<AgentInfo> agents = List.of(
                new AgentInfo(
                        ImageAnalysisAgent.ID,
                        "ImageAnalysisAgent",
                        ImageAnalysisAgent.DESCRIPTION,
                        "/api/agents/image/analyze",
                        "/agents/image/v1/chat/completions",
                        "http://127.0.0.1:8080/agents/image/v1"
                ),
                new AgentInfo(
                        TextAnalysisAgent.ID,
                        "TextAnalysisAgent",
                        TextAnalysisAgent.DESCRIPTION,
                        "/api/agents/text/analyze",
                        "/agents/text/v1/chat/completions",
                        "http://127.0.0.1:8080/agents/text/v1"
                ),
                new AgentInfo(
                        ResultAnalysisAgent.ID,
                        "ResultAnalysisAgent",
                        ResultAnalysisAgent.DESCRIPTION,
                        "/api/agents/result/analyze",
                        "/agents/result/v1/chat/completions",
                        "http://127.0.0.1:8080/agents/result/v1"
                )
        );
        return Map.of("agents", agents);
    }
}
