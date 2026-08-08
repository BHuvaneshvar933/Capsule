package com.example.tracker1.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CuratorItemRequest {
    
    private String url;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private List<String> tags;
    
    private boolean favorite;
}
