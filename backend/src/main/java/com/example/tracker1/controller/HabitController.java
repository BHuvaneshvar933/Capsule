package com.example.tracker1.controller;

import com.example.tracker1.model.entity.Habit;
import com.example.tracker1.model.entity.HabitLog;
import com.example.tracker1.repository.HabitLogRepository;
import com.example.tracker1.repository.HabitRepository;
import com.example.tracker1.repository.UserRepository;
import com.example.tracker1.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;

    public HabitController(HabitRepository habitRepository, HabitLogRepository habitLogRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.userRepository = userRepository;
    }

    private String getUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    // --- Habits ---

    @GetMapping
    public ResponseEntity<List<Habit>> getHabits() {
        String userId = getUserId();
        return ResponseEntity.ok(habitRepository.findByUserIdOrderByOrderAscCreatedAtAsc(userId));
    }

    @PostMapping
    public ResponseEntity<Habit> createHabit(@RequestBody Habit habit) {
        String userId = getUserId();
        habit.setUserId(userId);
        habit.setCreatedAt(Instant.now());
        habit.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(habitRepository.save(habit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@PathVariable String id, @RequestBody Habit patch) {
        String userId = getUserId();
        return habitRepository.findById(id)
                .filter(h -> h.getUserId().equals(userId))
                .map(h -> {
                    if (patch.getName() != null) h.setName(patch.getName());
                    if (patch.getIcon() != null) h.setIcon(patch.getIcon());
                    h.setUpdatedAt(Instant.now());
                    return ResponseEntity.ok(habitRepository.save(h));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable String id) {
        String userId = getUserId();
        Optional<Habit> habit = habitRepository.findById(id);
        if (habit.isPresent() && habit.get().getUserId().equals(userId)) {
            habitRepository.delete(habit.get());
            
            // Note: Ideally we would also remove this habit from all HabitLogs, 
            // but since they are stored as a map, we can either leave them (they won't render)
            // or we would need to run an update on all logs. We'll let the frontend handle ignoring deleted habits.
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<List<Habit>> resetHabits() {
        String userId = getUserId();
        
        // Delete all habits
        List<Habit> existing = habitRepository.findByUserIdOrderByOrderAscCreatedAtAsc(userId);
        habitRepository.deleteAll(existing);
        
        // Delete all logs
        habitLogRepository.deleteByUserId(userId);
        
        // Create defaults
        Instant now = Instant.now();
        List<Habit> defaults = List.of(
            Habit.builder().userId(userId).name("1 LeetCode Problem").icon("💻").order(0).createdAt(now).updatedAt(now).build(),
            Habit.builder().userId(userId).name("Revise 1 GATE Subject").icon("🎓").order(1).createdAt(now).updatedAt(now).build(),
            Habit.builder().userId(userId).name("Apply to 2 Jobs").icon("💼").order(2).createdAt(now).updatedAt(now).build(),
            Habit.builder().userId(userId).name("Read Tech Article").icon("📰").order(3).createdAt(now).updatedAt(now).build(),
            Habit.builder().userId(userId).name("Drink 8 Glasses Water").icon("💧").order(4).createdAt(now).updatedAt(now).build(),
            Habit.builder().userId(userId).name("30 min Exercise").icon("💪").order(5).createdAt(now).updatedAt(now).build()
        );
        
        List<Habit> saved = habitRepository.saveAll(defaults);
        return ResponseEntity.ok(saved);
    }

    // --- Habit Logs ---

    @GetMapping("/logs")
    public ResponseEntity<Map<String, Map<String, Boolean>>> getLogsForDays(@RequestParam List<String> days) {
        String userId = getUserId();
        List<HabitLog> logs = habitLogRepository.findByUserIdAndDayKeyIn(userId, days);
        
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        for (HabitLog log : logs) {
            result.put(log.getDayKey(), log.getCompletedHabits());
        }
        
        // Ensure all requested days exist in the map, even if empty
        for (String day : days) {
            result.putIfAbsent(day, new HashMap<>());
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logs/{dayKey}/{habitId}")
    public ResponseEntity<Map<String, Boolean>> setHabitDone(
            @PathVariable String dayKey,
            @PathVariable String habitId,
            @RequestParam boolean done) {
        
        String userId = getUserId();
        HabitLog log = habitLogRepository.findByUserIdAndDayKey(userId, dayKey)
                .orElse(HabitLog.builder()
                        .userId(userId)
                        .dayKey(dayKey)
                        .completedHabits(new HashMap<>())
                        .build());
        
        if (done) {
            log.getCompletedHabits().put(habitId, true);
        } else {
            log.getCompletedHabits().remove(habitId);
        }
        
        HabitLog saved = habitLogRepository.save(log);
        return ResponseEntity.ok(saved.getCompletedHabits());
    }
}
