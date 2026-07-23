package com.example.tracker1.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCoverLetterRequest {
    @NotBlank(message = "resumeId is required")
    private String resumeId;
    @NotBlank(message = "jobDescription is required")
    private String jobDescription;
    private String companyName;
}
