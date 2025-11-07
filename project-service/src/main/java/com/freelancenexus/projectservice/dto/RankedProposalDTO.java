package com.freelancenexus.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankedProposalDTO {
    private Long id;
    private Long freelancerId;
    private String coverLetter;
    private BigDecimal proposedBudget;
    private Integer deliveryDays;
    private BigDecimal aiScore;
    private Integer rank;
    private String aiAnalysis;
    private java.util.List<String> strengths;
    private java.util.List<String> concerns;
    private LocalDateTime submittedAt;
}