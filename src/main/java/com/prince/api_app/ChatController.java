package com.prince.api_app;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;




@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final BookTools bookTools;
    private final WeatherTools weatherTools;
    private final RecommendationTools recommendationTools;

    public ChatController(ChatClient.Builder builder,
                          BookTools bookTools,
                          WeatherTools weatherTools,
                          RecommendationTools recommendationTools,
                          ChatMemory chatMemory) {
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
        this.bookTools = bookTools;
        this.weatherTools = weatherTools;
        this.recommendationTools = recommendationTools;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .tools(bookTools, weatherTools, recommendationTools)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,
                        request.conversationId()))
                .call()
                .content();
    }
}