package com.example.tracker1.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CuratorItemResponse {
    private String id;
    private String url;
    private String type;
    private String title;
    private String description;
    private List<String> tags;
    private boolean favorite;
    private Instant createdAt;
    private Instant updatedAt;
}
