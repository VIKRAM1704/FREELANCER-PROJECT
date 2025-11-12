package com.freelancenexus.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProposalDTO;
import com.freelancenexus.projectservice.dto.ProposalSubmitDTO;
import com.freelancenexus.projectservice.service.ProposalService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProposalControllerTest {

    @Mock
    private ProposalService proposalService;

    @InjectMocks
    private ProposalController proposalController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ProposalSubmitDTO submitDTO;
    private ProposalDTO proposalDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(proposalController).build();
        objectMapper = new ObjectMapper();

        submitDTO = new ProposalSubmitDTO(1L, "This is a detailed cover letter exceeding 50 characters",
                BigDecimal.valueOf(1500), 15);

        proposalDTO = new ProposalDTO(1L, 1L, "Project A", 1L, "Cover letter...",
                BigDecimal.valueOf(1500), 15, BigDecimal.valueOf(90), null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldSubmitProposalSuccessfully() throws Exception {
        when(proposalService.submitProposal(eq(1L), any(ProposalSubmitDTO.class))).thenReturn(proposalDTO);

        mockMvc.perform(post("/api/projects/{projectId}/proposals", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectId").value(1));
    }

    @Test
    void shouldGetProjectProposalsWithoutRanking() throws Exception {
        when(proposalService.getProposalsByProjectId(1L)).thenReturn(List.of(proposalDTO));

        mockMvc.perform(get("/api/projects/{projectId}/proposals", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetProjectProposalsWithRanking() throws Exception {
        when(proposalService.getRankedProposalsByProjectId(1L)).thenReturn(List.of(proposalDTO));

        mockMvc.perform(get("/api/projects/{projectId}/proposals", 1L)
                        .param("ranked", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetProposalById() throws Exception {
        when(proposalService.getProposalById(1L)).thenReturn(proposalDTO);

        mockMvc.perform(get("/api/proposals/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldGetFreelancerProposals() throws Exception {
        when(proposalService.getProposalsByFreelancerId(1L)).thenReturn(List.of(proposalDTO));

        mockMvc.perform(get("/api/proposals/freelancer/{freelancerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldAcceptProposal() throws Exception {
        when(proposalService.acceptProposal(1L)).thenReturn(proposalDTO);

        mockMvc.perform(put("/api/proposals/{id}/accept", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldRejectProposal() throws Exception {
        when(proposalService.rejectProposal(1L)).thenReturn(proposalDTO);

        mockMvc.perform(put("/api/proposals/{id}/reject", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldHandleRuntimeException() throws Exception {
        when(proposalService.getProposalById(anyLong())).thenThrow(new RuntimeException("Error occurred"));

        mockMvc.perform(get("/api/proposals/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error occurred"));
    }
}
