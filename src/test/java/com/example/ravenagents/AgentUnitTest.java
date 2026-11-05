package com.example.ravenagents;

import com.example.ravenagents.agents.FakeLlmGateway;
import com.example.ravenagents.agents.ImageAnalysisAgent;
import com.example.ravenagents.agents.ResultAnalysisAgent;
import com.example.ravenagents.agents.TextAnalysisAgent;
import com.example.ravenagents.model.ImageAnalyzeRequest;
import com.example.ravenagents.model.ResultAnalyzeRequest;
import com.example.ravenagents.model.TextAnalyzeRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentUnitTest {

    private final FakeLlmGateway gateway = new FakeLlmGateway();

    @Test
    void imageAgentReturnsFakeMode() {
        ImageAnalysisAgent agent = new ImageAnalysisAgent(gateway);
        ImageAnalyzeRequest req = new ImageAnalyzeRequest();
        req.setDescription("测试图");
        var resp = agent.analyze(req);
        assertEquals("image-agent", resp.getAgentId());
        assertEquals("fake", resp.getMode());
        assertTrue(resp.getAnalysis().contains("测试图"));
    }

    @Test
    void textAgentReturnsFakeMode() {
        TextAnalysisAgent agent = new TextAnalysisAgent(gateway);
        TextAnalyzeRequest req = new TextAnalyzeRequest();
        req.setContent("你好世界");
        var resp = agent.analyze(req);
        assertEquals("text-agent", resp.getAgentId());
        assertTrue(resp.getAnalysis().contains("你好世界"));
    }

    @Test
    void resultAgentMergesFindings() {
        ResultAnalysisAgent agent = new ResultAnalysisAgent(gateway);
        ResultAnalyzeRequest req = new ResultAnalyzeRequest();
        req.setImageFindings("图：完整");
        req.setTextFindings("文：合格");
        var resp = agent.analyze(req);
        assertEquals("result-agent", resp.getAgentId());
        assertTrue(resp.getAnalysis().contains("图：完整"));
        assertTrue(resp.getAnalysis().contains("文：合格"));
    }
}
