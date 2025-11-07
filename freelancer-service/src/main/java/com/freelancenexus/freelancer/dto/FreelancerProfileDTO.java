package com.freelancenexus.freelancer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreelancerProfileDTO {
    
    private Long id;
    private Long userId;
    private String title;
    private String bio;
    private BigDecimal hourlyRate;
    private String availability;
    private BigDecimal totalEarnings;
    private Integer completedProjects;
    private BigDecimal averageRating;
    
    private List<SkillDTO> skills;
    private List<PortfolioDTO> portfolios;
    private List<RatingDTO> recentRatings;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}