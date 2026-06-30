package com.example.tracker1.controller;

import com.example.tracker1.model.dto.*;
import com.example.tracker1.service.AuthService;
import com.example.tracker1.service.JoiIngestionService;
import com.example.tracker1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JoiIngestionService joiIngestionService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        
        // Asynchronously synchronize the user's RAG context to prevent blocking the authentication flow.
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            CompletableFuture.runAsync(() -> {
                try {
                    joiIngestionService.indexUserData(user.getId(), user.getEmail());
                } catch (Exception e) {
                    System.err.println("Failed to index applications for Joi: " + e.getMessage());
                }
            });
        });
        
        return response;
    }
}
