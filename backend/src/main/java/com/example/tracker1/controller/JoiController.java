package com.example.tracker1.controller;

import com.example.tracker1.service.JoiService;
import com.example.tracker1.model.entity.User;
import com.example.tracker1.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller exposing endpoints for the Joi AI assistant.
 * Handles incoming chat requests, performs user authorization, and delegates processing to the JoiService.
 */
@RestController
@RequestMapping("/api/Joi")
public class JoiController {

    private final JoiService JoiService;
    private final UserRepository userRepository;

    public JoiController(JoiService JoiService, UserRepository userRepository) {
        this.JoiService = JoiService;
        this.userRepository = userRepository;
    }

    /**
     * Processes a chat request from the client.
     *
     * @param request        the request payload containing the user's message.
     * @param authentication the injected Spring Security authentication context.
     * @return a ResponseEntity containing the AI's response or an error payload.
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request, Authentication authentication) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }
        
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        String userId = userOpt.get().getId();
        
        try {
            String response = JoiService.chat(message, userId);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to communicate with Joi (AI provider may be offline)"));
        }
    }
}
