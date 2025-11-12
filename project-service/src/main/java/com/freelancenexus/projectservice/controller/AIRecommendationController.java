package com.freelancenexus.projectservice.controller;

import com.freelancenexus.projectservice.dto.AIRecommendationDTO;
import com.freelancenexus.projectservice.dto.ProjectSummaryDTO;
import com.freelancenexus.projectservice.dto.RankedProposalDTO;
import com.freelancenexus.projectservice.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIRecommendationController {

    private final AIService aiService;

    // Only freelancers can get AI recommendations
    @GetMapping("/recommendations/freelancer/{freelancerId}")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<List<AIRecommendationDTO>> getRecommendations(
            @PathVariable Long freelancerId,
            @RequestParam List<String> skills,
            @RequestParam(required = false) String bio) {
        log.info("GET /api/ai/recommendations/freelancer/{} - Getting AI recommendations", freelancerId);
        
        List<AIRecommendationDTO> recommendations = aiService.recommendProjectsForFreelancer(
                freelancerId, skills, bio);
        
        return ResponseEntity.ok(recommendations);
    }

    // Only clients can rank proposals with AI
    @GetMapping("/proposals/rank/{projectId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<RankedProposalDTO>> rankProposals(@PathVariable Long projectId) {
        log.info("GET /api/ai/proposals/rank/{} - Ranking proposals with AI", projectId);
        
        List<RankedProposalDTO> rankedProposals = aiService.rankProposalsForProject(projectId);
        
        return ResponseEntity.ok(rankedProposals);
    }

    // Both client and freelancer can view AI summary
    @GetMapping("/summary/project/{projectId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<ProjectSummaryDTO> getProjectSummary(@PathVariable Long projectId) {
        log.info("GET /api/ai/summary/project/{} - Generating AI summary", projectId);
        
        ProjectSummaryDTO summary = aiService.generateProjectSummary(projectId);
        
        return ResponseEntity.ok(summary);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Error in AIRecommendationController", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("AI service error: " + ex.getMessage());
    }
}