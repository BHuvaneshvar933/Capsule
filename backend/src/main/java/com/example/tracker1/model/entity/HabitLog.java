package com.example.tracker1.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "habit_logs")
@CompoundIndexes({
    @CompoundIndex(name = "user_day_idx", def = "{'userId': 1, 'dayKey': 1}", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitLog {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String dayKey; // e.g. "2026-08-12"
    
    private Map<String, Boolean> completedHabits; // habitId -> boolean
}
