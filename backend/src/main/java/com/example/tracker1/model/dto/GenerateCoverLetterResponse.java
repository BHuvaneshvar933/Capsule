package com.example.tracker1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCoverLetterResponse {
    private String coverLetter;
    private long aiDurationMs;
}
