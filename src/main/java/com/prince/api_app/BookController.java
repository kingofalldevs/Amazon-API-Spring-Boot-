package com.prince.api_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    @GetMapping("/books")
    public List<Book> books(){
        return "Hello from my World";
    }
}
