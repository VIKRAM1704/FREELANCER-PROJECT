package com.freelancenexus.projectservice.dto;

import com.freelancenexus.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private Long clientId;
    private String title;
    private String description;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private Integer durationDays;
    private List<String> requiredSkills;
    private String category;
    private ProjectStatus status;
    private LocalDate deadline;
    private Long assignedFreelancer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer proposalCount;
}