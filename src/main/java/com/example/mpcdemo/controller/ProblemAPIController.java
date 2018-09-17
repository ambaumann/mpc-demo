package com.example.mpcdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProblemAPIController {

	@RequestMapping("/hello")
    public String helloWorld() {
        return "Greetings from Spring Boot!";
    }
}
