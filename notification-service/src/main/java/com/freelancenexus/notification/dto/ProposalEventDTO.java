package com.freelancenexus.notification.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//ProposalEventDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalEventDTO {
 private Long proposalId;
 private Long projectId;
 private String projectTitle;
 private Long freelancerId;
 private String freelancerName;
 private String freelancerEmail;
 private Long clientId;
 private String clientEmail;
 private Double bidAmount;
 private String status;
 private LocalDateTime submittedAt;
}