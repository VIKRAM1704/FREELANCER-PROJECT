package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.*;
import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.Proposal;
import com.freelancenexus.projectservice.repository.ProjectRepository;
import com.freelancenexus.projectservice.repository.ProposalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final GeminiIntegrationService geminiService;
    private final ProjectRepository projectRepository;
    private final ProposalRepository proposalRepository;
    private final ObjectMapper objectMapper;

    /**
     * Recommend projects for a freelancer based on their skills and profile
     */
    public List<AIRecommendationDTO> recommendProjectsForFreelancer(Long freelancerId, List<String> freelancerSkills, String freelancerBio) {
        try {
            log.info("Getting AI recommendations for freelancer: {}", freelancerId);

            // Get all open projects
            List<Project> openProjects = projectRepository.findAllOpenProjects();
            
            if (openProjects.isEmpty()) {
                log.info("No open projects available");
                return Collections.emptyList();
            }

            // Build prompt for AI
            String prompt = buildRecommendationPrompt(freelancerSkills, freelancerBio, openProjects);
            
            // Call Gemini AI
            JsonNode aiResponse = geminiService.callGeminiForJson(prompt);
            
            // Parse and return recommendations
            return parseRecommendations(aiResponse, openProjects);

        } catch (Exception e) {
            log.error("Error getting AI recommendations", e);
            return fallbackRecommendations(freelancerId, freelancerSkills);
        }
    }

    /**
     * Rank proposals for a project using AI
     */
    public List<RankedProposalDTO> rankProposalsForProject(Long projectId) {
        try {
            log.info("Ranking proposals for project: {}", projectId);

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            List<Proposal> proposals = proposalRepository.findByProjectId(projectId);
            
            if (proposals.isEmpty()) {
                log.info("No proposals to rank");
                return Collections.emptyList();
            }

            // Build prompt for AI
            String prompt = buildRankingPrompt(project, proposals);
            
            // Call Gemini AI
            JsonNode aiResponse = geminiService.callGeminiForJson(prompt);
            
            // Parse rankings and update proposal scores
            List<RankedProposalDTO> rankedProposals = parseRankings(aiResponse, proposals);
            
            // Update AI scores in database
            updateProposalScores(rankedProposals);
            
            return rankedProposals;

        } catch (Exception e) {
            log.error("Error ranking proposals", e);
            return fallbackRanking(projectId);
        }
    }

    /**
     * Generate a natural language summary of a project
     */
    public ProjectSummaryDTO generateProjectSummary(Long projectId) {
        try {
            log.info("Generating summary for project: {}", projectId);

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            String prompt = buildSummaryPrompt(project);
            
            JsonNode aiResponse = geminiService.callGeminiForJson(prompt);
            
            return parseSummary(aiResponse, projectId);

        } catch (Exception e) {
            log.error("Error generating project summary", e);
            return fallbackSummary(projectId);
        }
    }

    // ==================== Private Helper Methods ====================

    private String buildRecommendationPrompt(List<String> skills, String bio, List<Project> projects) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant helping match freelancers with projects.\n\n");
        prompt.append("Freelancer Profile:\n");
        prompt.append("Skills: ").append(String.join(", ", skills)).append("\n");
        prompt.append("Bio: ").append(bio != null ? bio : "Not provided").append("\n\n");
        prompt.append("Available Projects:\n");

        for (int i = 0; i < Math.min(projects.size(), 20); i++) {
            Project p = projects.get(i);
            prompt.append(String.format("%d. %s (ID: %d)\n", i + 1, p.getTitle(), p.getId()));
            prompt.append("   Category: ").append(p.getCategory()).append("\n");
            prompt.append("   Budget: $").append(p.getBudgetMin()).append(" - $").append(p.getBudgetMax()).append("\n");
            prompt.append("   Duration: ").append(p.getDurationDays()).append(" days\n");
            prompt.append("   Required Skills: ").append(p.getRequiredSkills()).append("\n");
            prompt.append("   Description: ").append(p.getDescription().substring(0, Math.min(200, p.getDescription().length()))).append("...\n\n");
        }

        prompt.append("\nAnalyze the freelancer's skills and recommend the top 5 best-matching projects. ");
        prompt.append("For each recommendation, provide:\n");
        prompt.append("1. projectId\n");
        prompt.append("2. matchScore (0-100)\n");
        prompt.append("3. matchReason (brief explanation)\n");
        prompt.append("4. matchingSkills (array of skills that match)\n");
        prompt.append("5. skillMatchPercentage\n\n");
        prompt.append("Return your response as a JSON array with this structure:\n");
        prompt.append("```json\n");
        prompt.append("[{\"projectId\": 1, \"matchScore\": 95, \"matchReason\": \"...\", \"matchingSkills\": [...], \"skillMatchPercentage\": 80}]\n");
        prompt.append("```");

        return prompt.toString();
    }

    private String buildRankingPrompt(Project project, List<Proposal> proposals) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant helping rank freelancer proposals for a project.\n\n");
        prompt.append("Project Details:\n");
        prompt.append("Title: ").append(project.getTitle()).append("\n");
        prompt.append("Description: ").append(project.getDescription()).append("\n");
        prompt.append("Budget Range: $").append(project.getBudgetMin()).append(" - $").append(project.getBudgetMax()).append("\n");
        prompt.append("Duration: ").append(project.getDurationDays()).append(" days\n");
        prompt.append("Required Skills: ").append(project.getRequiredSkills()).append("\n\n");

        prompt.append("Proposals:\n");
        for (int i = 0; i < proposals.size(); i++) {
            Proposal prop = proposals.get(i);
            prompt.append(String.format("%d. Proposal ID: %d\n", i + 1, prop.getId()));
            prompt.append("   Freelancer ID: ").append(prop.getFreelancerId()).append("\n");
            prompt.append("   Proposed Budget: $").append(prop.getProposedBudget()).append("\n");
            prompt.append("   Delivery Days: ").append(prop.getDeliveryDays()).append("\n");
            prompt.append("   Cover Letter: ").append(prop.getCoverLetter()).append("\n\n");
        }

        prompt.append("\nRank these proposals from best to worst. For each proposal, provide:\n");
        prompt.append("1. proposalId\n");
        prompt.append("2. aiScore (0-100, higher is better)\n");
        prompt.append("3. rank (1 being best)\n");
        prompt.append("4. aiAnalysis (brief analysis)\n");
        prompt.append("5. strengths (array of positive points)\n");
        prompt.append("6. concerns (array of potential issues)\n\n");
        prompt.append("Consider: budget alignment, delivery time, cover letter quality, professionalism.\n\n");
        prompt.append("Return your response as a JSON array:\n");
        prompt.append("```json\n");
        prompt.append("[{\"proposalId\": 1, \"aiScore\": 95, \"rank\": 1, \"aiAnalysis\": \"...\", \"strengths\": [...], \"concerns\": [...]}]\n");
        prompt.append("```");

        return prompt.toString();
    }

    private String buildSummaryPrompt(Project project) {
        return String.format("""
            You are an AI assistant summarizing a freelance project.
            
            Project Details:
            Title: %s
            Description: %s
            Category: %s
            Budget: $%s - $%s
            Duration: %d days
            Required Skills: %s
            
            Provide a comprehensive summary with:
            1. summary (2-3 sentence overview)
            2. keyRequirements (main project requirements)
            3. idealCandidate (description of ideal freelancer)
            4. estimatedComplexity (Low/Medium/High with reasoning)
            5. suggestedSkills (array of recommended skills)
            
            Return as JSON:
            ```json
            {
              "summary": "...",
              "keyRequirements": "...",
              "idealCandidate": "...",
              "estimatedComplexity": "...",
              "suggestedSkills": [...]
            }
            ```
            """,
            project.getTitle(),
            project.getDescription(),
            project.getCategory(),
            project.getBudgetMin(),
            project.getBudgetMax(),
            project.getDurationDays(),
            project.getRequiredSkills()
        );
    }

    private List<AIRecommendationDTO> parseRecommendations(JsonNode response, List<Project> projects) {
        List<AIRecommendationDTO> recommendations = new ArrayList<>();
        
        try {
            if (response.isArray()) {
                Map<Long, Project> projectMap = projects.stream()
                        .collect(Collectors.toMap(Project::getId, p -> p));

                for (JsonNode node : response) {
                    Long projectId = node.get("projectId").asLong();
                    Project project = projectMap.get(projectId);
                    
                    if (project != null) {
                        AIRecommendationDTO dto = new AIRecommendationDTO();
                        dto.setProjectId(projectId);
                        dto.setProjectTitle(project.getTitle());
                        dto.setCategory(project.getCategory());
                        dto.setBudgetMin(project.getBudgetMin());
                        dto.setBudgetMax(project.getBudgetMax());
                        dto.setDurationDays(project.getDurationDays());
                        dto.setRequiredSkills(parseSkills(project.getRequiredSkills()));
                        dto.setMatchScore(BigDecimal.valueOf(node.get("matchScore").asDouble()));
                        dto.setMatchReason(node.get("matchReason").asText());
                        dto.setMatchingSkills(parseJsonArray(node.get("matchingSkills")));
                        dto.setSkillMatchPercentage(node.get("skillMatchPercentage").asInt());
                        
                        recommendations.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing recommendations", e);
        }
        
        return recommendations;
    }

    private List<RankedProposalDTO> parseRankings(JsonNode response, List<Proposal> proposals) {
        List<RankedProposalDTO> rankings = new ArrayList<>();
        
        try {
            if (response.isArray()) {
                Map<Long, Proposal> proposalMap = proposals.stream()
                        .collect(Collectors.toMap(Proposal::getId, p -> p));

                for (JsonNode node : response) {
                    Long proposalId = node.get("proposalId").asLong();
                    Proposal proposal = proposalMap.get(proposalId);
                    
                    if (proposal != null) {
                        RankedProposalDTO dto = new RankedProposalDTO();
                        dto.setId(proposalId);
                        dto.setFreelancerId(proposal.getFreelancerId());
                        dto.setCoverLetter(proposal.getCoverLetter());
                        dto.setProposedBudget(proposal.getProposedBudget());
                        dto.setDeliveryDays(proposal.getDeliveryDays());
                        dto.setAiScore(BigDecimal.valueOf(node.get("aiScore").asDouble()));
                        dto.setRank(node.get("rank").asInt());
                        dto.setAiAnalysis(node.get("aiAnalysis").asText());
                        dto.setStrengths(parseJsonArray(node.get("strengths")));
                        dto.setConcerns(parseJsonArray(node.get("concerns")));
                        dto.setSubmittedAt(proposal.getSubmittedAt());
                        
                        rankings.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing rankings", e);
        }
        
        rankings.sort(Comparator.comparing(RankedProposalDTO::getRank));
        return rankings;
    }

    private ProjectSummaryDTO parseSummary(JsonNode response, Long projectId) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();
        dto.setProjectId(projectId);
        
        try {
            dto.setSummary(response.get("summary").asText());
            dto.setKeyRequirements(response.get("keyRequirements").asText());
            dto.setIdealCandidate(response.get("idealCandidate").asText());
            dto.setEstimatedComplexity(response.get("estimatedComplexity").asText());
            dto.setSuggestedSkills(parseJsonArray(response.get("suggestedSkills")));
        } catch (Exception e) {
            log.error("Error parsing summary", e);
        }
        
        return dto;
    }

    private void updateProposalScores(List<RankedProposalDTO> rankings) {
        for (RankedProposalDTO ranking : rankings) {
            try {
                Proposal proposal = proposalRepository.findById(ranking.getId()).orElse(null);
                if (proposal != null) {
                    proposal.setAiScore(ranking.getAiScore());
                    proposalRepository.save(proposal);
                }
            } catch (Exception e) {
                log.error("Error updating proposal score for ID: {}", ranking.getId(), e);
            }
        }
    }

    private List<String> parseSkills(String skillsJson) {
        try {
            JsonNode node = objectMapper.readTree(skillsJson);
            List<String> skills = new ArrayList<>();
            node.forEach(skill -> skills.add(skill.asText()));
            return skills;
        } catch (Exception e) {
            return Arrays.asList(skillsJson.split(","));
        }
    }

    private List<String> parseJsonArray(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(node -> result.add(node.asText()));
        }
        return result;
    }

    // Fallback methods when AI fails
    private List<AIRecommendationDTO> fallbackRecommendations(Long freelancerId, List<String> skills) {
        return Collections.emptyList();
    }

    private List<RankedProposalDTO> fallbackRanking(Long projectId) {
        return Collections.emptyList();
    }

    private ProjectSummaryDTO fallbackSummary(Long projectId) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();
        dto.setProjectId(projectId);
        dto.setSummary("Summary generation failed");
        return dto;
    }
}