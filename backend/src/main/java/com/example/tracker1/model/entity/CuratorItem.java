package com.example.tracker1.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "curator_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuratorItem {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String url;

    @Indexed
    private String type;

    private String title;

    private String description;

    @Indexed
    private List<String> tags;

    @Indexed
    private boolean favorite;

    @Indexed
    private Instant createdAt;

    @Indexed
    private Instant updatedAt;
}
