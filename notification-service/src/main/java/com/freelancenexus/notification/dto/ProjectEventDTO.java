package com.freelancenexus.notification.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//ProjectEventDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEventDTO {
 private Long projectId;
 private Long clientId;
 private String clientEmail;
 private String projectTitle;
 private String description;
 private Double budget;
 private String status;
 private Long assignedFreelancerId;
 private String freelancerName;
 private String freelancerEmail;
 private LocalDateTime createdAt;
}