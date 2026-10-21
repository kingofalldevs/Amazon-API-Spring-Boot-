package com.prince.api_app;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String chat(Model model) {
        model.addAttribute("conversationId", UUID.randomUUID().toString());
        return "chat";
    }
}
