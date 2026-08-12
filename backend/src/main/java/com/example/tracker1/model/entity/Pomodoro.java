package com.example.tracker1.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "pomodoros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pomodoro {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private int duration;
    
    private String type; // e.g. "work", "shortBreak", "longBreak"
    
    private String taskTitle;
    
    private List<String> tags;
    
    @Indexed
    private Instant completedAt;
}
