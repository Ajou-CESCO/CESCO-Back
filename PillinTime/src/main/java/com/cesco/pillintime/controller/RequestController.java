package com.cesco.pillintime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class RequestController {

    @PostMapping("/request")
    public String createRequest() {

        return "Hello";
    }

    @GetMapping("/request")
    public String getRequest() {
        return "Hello";
    }

}
