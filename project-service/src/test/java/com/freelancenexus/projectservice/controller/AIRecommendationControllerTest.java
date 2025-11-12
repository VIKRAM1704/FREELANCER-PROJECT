package com.freelancenexus.projectservice.controller;

import com.freelancenexus.projectservice.dto.AIRecommendationDTO;
import com.freelancenexus.projectservice.dto.ProjectSummaryDTO;
import com.freelancenexus.projectservice.dto.RankedProposalDTO;
import com.freelancenexus.projectservice.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AIRecommendationControllerTest {

    @Mock
    private AIService aiService;

    @InjectMocks
    private AIRecommendationController aiRecommendationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiRecommendationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnRecommendationsForFreelancer() throws Exception {
        AIRecommendationDTO dto = new AIRecommendationDTO(1L, "Project A", "IT",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(5000),
                30, Arrays.asList("Java", "Spring"), BigDecimal.valueOf(95),
                "High match", Arrays.asList("Java"), 100);

        when(aiService.recommendProjectsForFreelancer(eq(1L), anyList(), nullable(String.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ai/recommendations/freelancer/{freelancerId}", 1L)
                        .param("skills", "Java", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectId").value(1))
                .andExpect(jsonPath("$[0].projectTitle").value("Project A"))
                .andExpect(jsonPath("$[0].matchScore").value(95));
    }

    @Test
    void shouldReturnRankedProposalsForProject() throws Exception {
        RankedProposalDTO rankedProposal = new RankedProposalDTO(1L, 2L, "Cover letter...",
                BigDecimal.valueOf(1500), 15, BigDecimal.valueOf(90), 1,
                "Good match", List.of("Java"), List.of(), LocalDateTime.now());

        when(aiService.rankProposalsForProject(1L)).thenReturn(List.of(rankedProposal));

        mockMvc.perform(get("/api/ai/proposals/rank/{projectId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].aiScore").value(90));
    }

    @Test
    void shouldReturnProjectSummary() throws Exception {
        ProjectSummaryDTO summary = new ProjectSummaryDTO(1L, "Summary", "Key requirements",
                "Ideal candidate", "Medium", Arrays.asList("Java", "Spring"));

        when(aiService.generateProjectSummary(1L)).thenReturn(summary);

        mockMvc.perform(get("/api/ai/summary/project/{projectId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.summary").value("Summary"))
                .andExpect(jsonPath("$.suggestedSkills[0]").value("Java"));
    }

    @Test
    void shouldHandleRuntimeException() throws Exception {
        when(aiService.generateProjectSummary(anyLong()))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/ai/summary/project/{projectId}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("AI service error: Something went wrong"));
    }
}
