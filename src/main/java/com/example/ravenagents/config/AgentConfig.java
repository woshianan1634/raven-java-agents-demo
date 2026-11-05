package com.example.ravenagents.config;

import com.example.ravenagents.agents.LlmGateway;
import com.example.ravenagents.agents.ChatModelLlmGateway;
import com.example.ravenagents.agents.FakeLlmGateway;
import com.example.ravenagents.agents.ImageAnalysisAgent;
import com.example.ravenagents.agents.ResultAnalysisAgent;
import com.example.ravenagents.agents.TextAnalysisAgent;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AgentConfig {

    @Bean
    public LlmGateway llmGateway(AppProperties props) {
        if (props.hasApiKey()) {
            ChatModel model = OpenAiChatModel.builder()
                    .apiKey(props.getApiKey())
                    .baseUrl(props.getBaseUrl())
                    .modelName(props.getModel())
                    .timeout(Duration.ofSeconds(60))
                    .build();
            return new ChatModelLlmGateway(model);
        }
        return new FakeLlmGateway();
    }

    @Bean
    public ImageAnalysisAgent imageAnalysisAgent(LlmGateway llmGateway) {
        return new ImageAnalysisAgent(llmGateway);
    }

    @Bean
    public TextAnalysisAgent textAnalysisAgent(LlmGateway llmGateway) {
        return new TextAnalysisAgent(llmGateway);
    }

    @Bean
    public ResultAnalysisAgent resultAnalysisAgent(LlmGateway llmGateway) {
        return new ResultAnalysisAgent(llmGateway);
    }
}
