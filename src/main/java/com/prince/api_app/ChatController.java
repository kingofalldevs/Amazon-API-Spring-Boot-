package com.prince.api_app;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final BookTools bookTools;

    public ChatController(ChatClient.Builder builder, BookTools bookTools) {
        this.chatClient = builder.build();
        this.bookTools = bookTools;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .tools(bookTools)
                .call()
                .content();
    }
}