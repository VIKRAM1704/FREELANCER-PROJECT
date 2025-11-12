package com.freelancenexus.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProposalDTO;
import com.freelancenexus.projectservice.dto.ProposalSubmitDTO;
import com.freelancenexus.projectservice.model.ProposalStatus;
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

import static org.mockito.ArgumentMatchers.any;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProposalDTO sampleProposal;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(proposalController)
                .setControllerAdvice(proposalController)
                .build();

        sampleProposal = new ProposalDTO(1L, 1L, "Project", 1L, "Cover letter",
                BigDecimal.valueOf(100), 5, BigDecimal.valueOf(90), ProposalStatus.PENDING,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldSubmitProposal_whenPostCalled() throws Exception {
        ProposalSubmitDTO submitDTO = new ProposalSubmitDTO(1L, "Cover letter with enough characters 1234567890",
                BigDecimal.valueOf(100), 5);
        when(proposalService.submitProposal(any(Long.class), any(ProposalSubmitDTO.class))).thenReturn(sampleProposal);

        mockMvc.perform(post("/api/projects/1/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProposal)));
    }

    @Test
    void shouldGetProposal_whenGetByIdCalled() throws Exception {
        when(proposalService.getProposalById(1L)).thenReturn(sampleProposal);

        mockMvc.perform(get("/api/proposals/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProposal)));
    }

    @Test
    void shouldAcceptProposal_whenPutAcceptCalled() throws Exception {
        when(proposalService.acceptProposal(1L)).thenReturn(sampleProposal);

        mockMvc.perform(put("/api/proposals/1/accept"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProposal)));
    }

    @Test
    void shouldRejectProposal_whenPutRejectCalled() throws Exception {
        when(proposalService.rejectProposal(1L)).thenReturn(sampleProposal);

        mockMvc.perform(put("/api/proposals/1/reject"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProposal)));
    }

    @Test
    void shouldReturnBadRequest_whenServiceThrowsException() throws Exception {
        when(proposalService.getProposalById(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/proposals/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not found"));
    }
}
