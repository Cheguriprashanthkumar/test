package org.nishgrid.clienterp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health")
    public String checkHealth() {
        // This message is the key. We want to see it in the terminal.
        System.out.println(">>> Health Check OK! Your project has been rebuilt successfully at: " + LocalDateTime.now());
        return "Backend is running!";
    }
}