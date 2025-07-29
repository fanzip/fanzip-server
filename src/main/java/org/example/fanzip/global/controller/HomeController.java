package org.example.fanzip.global.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class HomeController {
    @GetMapping("/")
    public String home() {
        log.info("================> HomeController /");
        return "login";
    }
    
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<String> health() {
        log.info("================> Health check");
        return ResponseEntity.ok("Application is running!");
    }
}