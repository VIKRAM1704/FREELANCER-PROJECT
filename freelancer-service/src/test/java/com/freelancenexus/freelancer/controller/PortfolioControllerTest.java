package com.freelancenexus.freelancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.freelancer.dto.PortfolioDTO;
import com.freelancenexus.freelancer.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    private PortfolioDTO portfolioDTO;

    @BeforeEach
    void setUp() {
        portfolioDTO = new PortfolioDTO();
        portfolioDTO.setId(1L);
        portfolioDTO.setFreelancerId(100L);
        portfolioDTO.setTitle("Sample Project");
        portfolioDTO.setDescription("A project description");
    }

    @Test
    void shouldAddPortfolio() throws Exception {
        when(portfolioService.addPortfolio(eq(100L), any(PortfolioDTO.class))).thenReturn(portfolioDTO);

        mockMvc.perform(post("/api/freelancers/100/portfolio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.freelancerId").value(100L))
                .andExpect(jsonPath("$.title").value("Sample Project"));
    }

    @Test
    void shouldGetFreelancerPortfolios() throws Exception {
        when(portfolioService.getFreelancerPortfolios(100L)).thenReturn(List.of(portfolioDTO));

        mockMvc.perform(get("/api/freelancers/100/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].freelancerId").value(100L));
    }

    @Test
    void shouldGetPortfolioById() throws Exception {
        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolioDTO);

        mockMvc.perform(get("/api/freelancers/portfolio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Sample Project"));
    }

    @Test
    void shouldUpdatePortfolio() throws Exception {
        portfolioDTO.setTitle("Updated Project");
        when(portfolioService.updatePortfolio(eq(1L), any(PortfolioDTO.class))).thenReturn(portfolioDTO);

        mockMvc.perform(put("/api/freelancers/portfolio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Project"));
    }

    @Test
    void shouldDeletePortfolio() throws Exception {
        doNothing().when(portfolioService).deletePortfolio(1L);

        mockMvc.perform(delete("/api/freelancers/portfolio/1"))
                .andExpect(status().isNoContent());
    }
}
