package com.freelancenexus.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.AIRecommendationDTO;
import com.freelancenexus.projectservice.dto.ProjectSummaryDTO;
import com.freelancenexus.projectservice.dto.RankedProposalDTO;
import com.freelancenexus.projectservice.service.AIService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AIRecommendationControllerTest {

    @Mock
    private AIService aiService;

    @InjectMocks
    private AIRecommendationController aiController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiController)
                .setControllerAdvice(aiController)
                .build();
    }

    @Test
    void shouldReturnRecommendations_whenGetRecommendationsCalled() throws Exception {
        AIRecommendationDTO recommendation = new AIRecommendationDTO(1L, "Test Project", "IT",
                BigDecimal.valueOf(100), BigDecimal.valueOf(500), 10, List.of("Java"), 
                BigDecimal.valueOf(95), "Skills match", List.of("Java"), 100);
        when(aiService.recommendProjectsForFreelancer(eq(1L), anyList(), anyString()))
                .thenReturn(List.of(recommendation));

        mockMvc.perform(get("/api/ai/recommendations/freelancer/1")
                        .param("skills", "Java"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(recommendation))));
    }

    @Test
    void shouldReturnRankedProposals_whenRankProposalsCalled() throws Exception {
        RankedProposalDTO ranked = new RankedProposalDTO(1L, 1L, "Good", BigDecimal.valueOf(200),
                5, BigDecimal.valueOf(90), 1, "AI analysis", List.of("Skill1"), List.of("Concern"), null);
        when(aiService.rankProposalsForProject(1L)).thenReturn(List.of(ranked));

        mockMvc.perform(get("/api/ai/proposals/rank/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(ranked))));
    }

    @Test
    void shouldReturnProjectSummary_whenGetProjectSummaryCalled() throws Exception {
        ProjectSummaryDTO summary = new ProjectSummaryDTO(1L, "Summary", "Key Req", "Ideal", "Low", List.of("Java"));
        when(aiService.generateProjectSummary(1L)).thenReturn(summary);

        mockMvc.perform(get("/api/ai/summary/project/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(summary)));
    }

    @Test
    void shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        when(aiService.generateProjectSummary(1L)).thenThrow(new RuntimeException("AI failed"));

        mockMvc.perform(get("/api/ai/summary/project/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("AI service error: AI failed"));
    }
}
