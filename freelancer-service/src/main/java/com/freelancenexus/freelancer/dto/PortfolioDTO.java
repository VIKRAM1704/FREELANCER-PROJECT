package com.freelancenexus.freelancer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    
    private Long id;
    
    @NotBlank(message = "Portfolio title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @Size(max = 500, message = "Project URL must not exceed 500 characters")
    private String projectUrl;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    @Size(max = 1000, message = "Technologies used must not exceed 1000 characters")
    private String technologiesUsed;
    
    @PastOrPresent(message = "Completion date cannot be in the future")
    private LocalDate completionDate;
    
    private LocalDateTime createdAt;
}