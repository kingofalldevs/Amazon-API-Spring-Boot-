package com.prince.api_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class orderController {

    @GetMapping("/orders")
    public String order(){
        return "The orders will be here";
    }


}
