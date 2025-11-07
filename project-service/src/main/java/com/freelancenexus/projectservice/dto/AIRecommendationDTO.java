package com.freelancenexus.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendationDTO {
    private Long projectId;
    private String projectTitle;
    private String category;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private Integer durationDays;
    private List<String> requiredSkills;
    private BigDecimal matchScore; // 0-100
    private String matchReason;
    private List<String> matchingSkills;
    private Integer skillMatchPercentage;
}