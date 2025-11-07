package com.freelancenexus.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDTO {
    private Long projectId;
    private String summary;
    private String keyRequirements;
    private String idealCandidate;
    private String estimatedComplexity;
    private java.util.List<String> suggestedSkills;
}