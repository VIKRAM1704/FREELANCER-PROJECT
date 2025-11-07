package com.freelancenexus.projectservice.dto;

import com.freelancenexus.projectservice.model.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalDTO {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private Long freelancerId;
    private String coverLetter;
    private BigDecimal proposedBudget;
    private Integer deliveryDays;
    private BigDecimal aiScore;
    private ProposalStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
}