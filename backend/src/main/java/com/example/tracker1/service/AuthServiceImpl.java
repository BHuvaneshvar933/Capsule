package com.example.tracker1.service;

import com.example.tracker1.config.JwtUtil;
import com.example.tracker1.model.dto.*;
import com.example.tracker1.model.entity.User;
import com.example.tracker1.exception.BadRequestException;
import com.example.tracker1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse googleLogin(GoogleAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList("242936215617-hgcpav88686rqjgg1qo0k9c5p579e6dp.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    // Create new user if not exists
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            // Set a random password since they authenticate via Google
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .role("ROLE_USER")
                            .build();
                    return userRepository.save(newUser);
                });

                String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

                return AuthResponse.builder()
                        .token(token)
                        .build();
            } else {
                throw new BadRequestException("Invalid Google ID token.");
            }
        } catch (Exception e) {
            throw new BadRequestException("Google authentication failed.");
        }
    }
}
