package com.example.ravenagents;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AgentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listAgents() throws Exception {
        mockMvc.perform(get("/api/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agents", hasSize(3)))
                .andExpect(jsonPath("$.agents[0].id").value("image-agent"))
                .andExpect(jsonPath("$.agents[1].id").value("text-agent"))
                .andExpect(jsonPath("$.agents[2].id").value("result-agent"));
    }

    @Test
    void imageDomainAnalyzeUsesFakeModeOffline() throws Exception {
        mockMvc.perform(post("/api/agents/image/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "一张产品包装照片",
                                  "imageUrl": "https://example.com/pack.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value("image-agent"))
                .andExpect(jsonPath("$.mode").value("fake"))
                .andExpect(jsonPath("$.analysis", containsString("[fake]")));
    }

    @Test
    void textDomainAnalyzeUsesFakeModeOffline() throws Exception {
        mockMvc.perform(post("/api/agents/text/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "这是一段需要分析的产品说明文字。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value("text-agent"))
                .andExpect(jsonPath("$.mode").value("fake"))
                .andExpect(jsonPath("$.analysis", containsString("产品说明")));
    }

    @Test
    void resultDomainAnalyzeSynthesizesUpstream() throws Exception {
        mockMvc.perform(post("/api/agents/result/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "imageFindings": "包装完整，标签清晰",
                                  "textFindings": "文案声称有机认证",
                                  "instruction": "检查图文是否一致"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value("result-agent"))
                .andExpect(jsonPath("$.mode").value("fake"))
                .andExpect(jsonPath("$.analysis", containsString("imageFindings")));
    }

    @Test
    void openAiCompatibleImageChatCompletions() throws Exception {
        mockMvc.perform(post("/agents/image/v1/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "model": "image-agent",
                                  "messages": [
                                    {"role": "user", "content": "描述这张图里的包装信息"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.object").value("chat.completion"))
                .andExpect(jsonPath("$.choices[0].message.role").value("assistant"))
                .andExpect(jsonPath("$.choices[0].message.content", containsString("[fake]")))
                .andExpect(jsonPath("$.choices[0].finish_reason").value("stop"));
    }

    @Test
    void openAiCompatibleTextChatCompletions() throws Exception {
        mockMvc.perform(post("/agents/text/v1/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "model": "text-agent",
                                  "messages": [
                                    {"role": "user", "content": "请摘要这段文字：春季促销即将开始"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices[0].message.content", containsString("春季促销")));
    }

    @Test
    void openAiCompatibleResultChatCompletions() throws Exception {
        mockMvc.perform(post("/agents/result/v1/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "model": "result-agent",
                                  "messages": [
                                    {"role": "user", "content": "imageFindings: ok\\ntextFindings: ok"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("result-agent"))
                .andExpect(jsonPath("$.choices", hasSize(1)));
    }

    @Test
    void streamNotSupported() throws Exception {
        mockMvc.perform(post("/agents/text/v1/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "model": "text-agent",
                                  "stream": true,
                                  "messages": [{"role": "user", "content": "hi"}]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
