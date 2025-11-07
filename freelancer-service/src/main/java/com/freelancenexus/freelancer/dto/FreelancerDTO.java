package com.freelancenexus.freelancer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 5000, message = "Bio must not exceed 5000 characters")
    private String bio;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly rate must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid hourly rate format")
    private BigDecimal hourlyRate;
    
    @Pattern(regexp = "AVAILABLE|BUSY|UNAVAILABLE", message = "Invalid availability status")
    private String availability;
    
    private BigDecimal totalEarnings;
    private Integer completedProjects;
    private BigDecimal averageRating;
    
    private List<SkillDTO> skills;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}