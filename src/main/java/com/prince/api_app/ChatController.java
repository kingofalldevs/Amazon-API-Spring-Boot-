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

    public ChatController(ChatClient.Builder builder,
                          BookTools bookTools,
                          ChatMemory chatMemory) {
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
        this.bookTools = bookTools;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .tools(bookTools)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,
                        request.conversationId()))
                .call()
                .content();
    }
}