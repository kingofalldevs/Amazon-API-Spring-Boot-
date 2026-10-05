package com.prince.api_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    @GetMapping("/home")
    public String customers(){
        return "Hello from my World";
    }
}
