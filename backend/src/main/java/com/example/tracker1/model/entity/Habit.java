package com.example.tracker1.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habit {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String name;
    
    private String icon;
    
    private int order;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}
