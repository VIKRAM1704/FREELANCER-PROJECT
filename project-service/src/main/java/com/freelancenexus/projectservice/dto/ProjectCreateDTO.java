package com.freelancenexus.projectservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Minimum budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum budget must be greater than 0")
    private BigDecimal budgetMin;

    @NotNull(message = "Maximum budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum budget must be greater than 0")
    private BigDecimal budgetMax;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotEmpty(message = "At least one skill is required")
    private List<String> requiredSkills;

    @NotBlank(message = "Category is required")
    private String category;

    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;
}