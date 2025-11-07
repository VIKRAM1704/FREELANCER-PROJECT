package com.freelancenexus.freelancer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    
    private Long id;
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    private Long projectId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;
    
    @Size(max = 2000, message = "Review must not exceed 2000 characters")
    private String review;
    
    private LocalDateTime createdAt;
}