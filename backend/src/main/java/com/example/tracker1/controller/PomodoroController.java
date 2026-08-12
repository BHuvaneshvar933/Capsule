package com.example.tracker1.controller;

import com.example.tracker1.model.entity.Pomodoro;
import com.example.tracker1.repository.PomodoroRepository;
import com.example.tracker1.repository.UserRepository;
import com.example.tracker1.util.SecurityUtil;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pomodoros")
public class PomodoroController {

    private final PomodoroRepository pomodoroRepository;
    private final UserRepository userRepository;

    public PomodoroController(PomodoroRepository pomodoroRepository, UserRepository userRepository) {
        this.pomodoroRepository = pomodoroRepository;
        this.userRepository = userRepository;
    }

    private String getUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping
    public ResponseEntity<List<Pomodoro>> getPomodoros(@RequestParam(required = false) String since) {
        String userId = getUserId();
        List<Pomodoro> pomodoros = pomodoroRepository.findByUserIdOrderByCompletedAtDesc(userId);
        
        if (since != null) {
            try {
                Instant sinceInstant = Instant.parse(since);
                pomodoros = pomodoros.stream()
                        .filter(p -> p.getCompletedAt() != null && !p.getCompletedAt().isBefore(sinceInstant))
                        .toList();
            } catch (Exception e) {
                // Ignore parsing errors and return all
            }
        }
        
        return ResponseEntity.ok(pomodoros);
    }

    @PostMapping
    public ResponseEntity<Pomodoro> createPomodoro(@RequestBody Pomodoro pomodoro) {
        String userId = getUserId();
        pomodoro.setUserId(userId);
        if (pomodoro.getCompletedAt() == null) {
            pomodoro.setCompletedAt(Instant.now());
        }
        Pomodoro saved = pomodoroRepository.save(pomodoro);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllPomodoros() {
        String userId = getUserId();
        List<Pomodoro> pomodoros = pomodoroRepository.findByUserIdOrderByCompletedAtDesc(userId);
        pomodoroRepository.deleteAll(pomodoros);
        return ResponseEntity.noContent().build();
    }
}
