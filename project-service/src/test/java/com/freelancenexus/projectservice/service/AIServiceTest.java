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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock private GeminiIntegrationService geminiService;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProposalRepository proposalRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private AIService aiService;

    private Project sampleProject;
    private Proposal sampleProposal;

    @BeforeEach
    void setUp() {
        sampleProject = new Project();
        sampleProject.setId(1L);
        sampleProject.setTitle("Test Project");
        sampleProject.setCategory("IT");
        sampleProject.setBudgetMin(BigDecimal.valueOf(100));
        sampleProject.setBudgetMax(BigDecimal.valueOf(500));
        sampleProject.setDurationDays(10);
        sampleProject.setRequiredSkills("[\"Java\",\"Spring\"]");

        sampleProposal = new Proposal();
        sampleProposal.setId(1L);
        sampleProposal.setFreelancerId(1L);
        sampleProposal.setProposedBudget(BigDecimal.valueOf(200));
        sampleProposal.setDeliveryDays(5);
        sampleProposal.setCoverLetter("Cover letter");
    }

    @Test
    void shouldReturnRecommendations_whenProjectsExist() throws Exception {
        when(projectRepository.findAllOpenProjects()).thenReturn(List.of(sampleProject));
        JsonNode fakeResponse = mock(JsonNode.class);
        when(fakeResponse.isArray()).thenReturn(false);
        when(geminiService.callGeminiForJson(anyString())).thenReturn(fakeResponse);

        List<AIRecommendationDTO> recommendations = aiService.recommendProjectsForFreelancer(1L, List.of("Java"), "Bio");

        assertThat(recommendations).isEmpty(); // fallback returns empty list
        verify(projectRepository, times(1)).findAllOpenProjects();
        verify(geminiService, times(1)).callGeminiForJson(anyString());
    }

    @Test
    void shouldReturnEmpty_whenNoOpenProjects() {
        when(projectRepository.findAllOpenProjects()).thenReturn(Collections.emptyList());

        List<AIRecommendationDTO> result = aiService.recommendProjectsForFreelancer(1L, List.of("Java"), null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnFallbackRanking_whenExceptionThrown() {
        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        List<RankedProposalDTO> ranked = aiService.rankProposalsForProject(1L);

        assertThat(ranked).isEmpty();
    }

    @Test
    void shouldReturnFallbackSummary_whenExceptionThrown() {
        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        ProjectSummaryDTO summary = aiService.generateProjectSummary(1L);

        assertThat(summary.getProjectId()).isEqualTo(1L);
        assertThat(summary.getSummary()).isEqualTo("Summary generation failed");
    }
}
