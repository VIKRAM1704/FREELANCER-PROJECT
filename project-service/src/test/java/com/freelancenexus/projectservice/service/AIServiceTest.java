package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.AIRecommendationDTO;
import com.freelancenexus.projectservice.dto.ProjectSummaryDTO;
import com.freelancenexus.projectservice.dto.RankedProposalDTO;
import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.Proposal;
import com.freelancenexus.projectservice.repository.ProjectRepository;
import com.freelancenexus.projectservice.repository.ProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private GeminiIntegrationService geminiService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AIService aiService;

    private Project project;
    private Proposal proposal;
    private JsonNode mockJsonNode;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setCategory("IT");
        project.setBudgetMin(BigDecimal.valueOf(1000));
        project.setBudgetMax(BigDecimal.valueOf(5000));
        project.setDurationDays(30);
        project.setRequiredSkills("[\"Java\", \"Spring\"]");
        project.setDescription("This is a test project");

        proposal = new Proposal();
        proposal.setId(1L);
        proposal.setFreelancerId(2L);
        proposal.setCoverLetter("Cover letter...");
        proposal.setProposedBudget(BigDecimal.valueOf(1500));
        proposal.setDeliveryDays(15);
    }

    @Test
    void shouldReturnRecommendations_whenProjectsExist() throws Exception {
        when(projectRepository.findAllOpenProjects()).thenReturn(List.of(project));
        when(geminiService.callGeminiForJson(anyString())).thenReturn(mock(JsonNode.class));

        List<AIRecommendationDTO> recommendations = aiService.recommendProjectsForFreelancer(1L, Arrays.asList("Java"), "Bio");

        assertNotNull(recommendations);
    }

    @Test
    void shouldReturnEmptyRecommendations_whenNoOpenProjects() {
        when(projectRepository.findAllOpenProjects()).thenReturn(List.of());

        List<AIRecommendationDTO> recommendations = aiService.recommendProjectsForFreelancer(1L, Arrays.asList("Java"), "Bio");

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldFallbackRecommendations_whenExceptionOccurs() throws Exception {
        when(projectRepository.findAllOpenProjects()).thenThrow(new RuntimeException("DB error"));

        List<AIRecommendationDTO> recommendations = aiService.recommendProjectsForFreelancer(1L, Arrays.asList("Java"), "Bio");

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldRankProposalsSuccessfully() throws Exception {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(proposalRepository.findByProjectId(1L)).thenReturn(List.of(proposal));
        when(geminiService.callGeminiForJson(anyString())).thenReturn(mock(JsonNode.class));
        when(proposalRepository.findById(anyLong())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(any(Proposal.class))).thenReturn(proposal);

        List<RankedProposalDTO> rankings = aiService.rankProposalsForProject(1L);

        assertNotNull(rankings);
    }

    @Test
    void shouldReturnEmptyRanking_whenNoProposals() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(proposalRepository.findByProjectId(1L)).thenReturn(List.of());

        List<RankedProposalDTO> rankings = aiService.rankProposalsForProject(1L);

        assertTrue(rankings.isEmpty());
    }

    @Test
    void shouldFallbackRanking_whenExceptionOccurs() {
        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        List<RankedProposalDTO> rankings = aiService.rankProposalsForProject(1L);

        assertTrue(rankings.isEmpty());
    }

    @Test
    void shouldGenerateProjectSummarySuccessfully() throws Exception {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(geminiService.callGeminiForJson(anyString())).thenReturn(mock(JsonNode.class));

        ProjectSummaryDTO summary = aiService.generateProjectSummary(1L);

        assertNotNull(summary);
        assertEquals(1L, summary.getProjectId());
    }

    @Test
    void shouldFallbackSummary_whenExceptionOccurs() {
        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        ProjectSummaryDTO summary = aiService.generateProjectSummary(1L);

        assertNotNull(summary);
        assertEquals("Summary generation failed", summary.getSummary());
    }
}
