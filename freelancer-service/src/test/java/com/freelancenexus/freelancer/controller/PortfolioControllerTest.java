package com.freelancenexus.freelancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.freelancer.dto.PortfolioDTO;
import com.freelancenexus.freelancer.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private PortfolioDTO portfolioDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();
        objectMapper = new ObjectMapper();

        portfolioDTO = new PortfolioDTO(
                1L,
                "My Project",
                "Project Description",
                "http://project-url.com",
                "http://image-url.com",
                "Java, Spring Boot",
                LocalDate.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldAddPortfolio() throws Exception {
        when(portfolioService.addPortfolio(eq(1L), any(PortfolioDTO.class))).thenReturn(portfolioDTO);

        mockMvc.perform(post("/api/freelancers/1/portfolio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(portfolioDTO.getId()))
                .andExpect(jsonPath("$.title").value(portfolioDTO.getTitle()));

        verify(portfolioService, times(1)).addPortfolio(eq(1L), any(PortfolioDTO.class));
    }

    @Test
    void shouldGetFreelancerPortfolios() throws Exception {
        when(portfolioService.getFreelancerPortfolios(1L)).thenReturn(List.of(portfolioDTO));

        mockMvc.perform(get("/api/freelancers/1/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(portfolioDTO.getId()))
                .andExpect(jsonPath("$[0].title").value(portfolioDTO.getTitle()));

        verify(portfolioService, times(1)).getFreelancerPortfolios(1L);
    }

    @Test
    void shouldGetPortfolioById() throws Exception {
        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolioDTO);

        mockMvc.perform(get("/api/freelancers/portfolio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(portfolioDTO.getId()))
                .andExpect(jsonPath("$.title").value(portfolioDTO.getTitle()));

        verify(portfolioService, times(1)).getPortfolioById(1L);
    }

    @Test
    void shouldUpdatePortfolio() throws Exception {
        when(portfolioService.updatePortfolio(eq(1L), any(PortfolioDTO.class))).thenReturn(portfolioDTO);

        mockMvc.perform(put("/api/freelancers/portfolio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(portfolioDTO.getId()))
                .andExpect(jsonPath("$.title").value(portfolioDTO.getTitle()));

        verify(portfolioService, times(1)).updatePortfolio(eq(1L), any(PortfolioDTO.class));
    }

    @Test
    void shouldDeletePortfolio() throws Exception {
        doNothing().when(portfolioService).deletePortfolio(1L);

        mockMvc.perform(delete("/api/freelancers/portfolio/1"))
                .andExpect(status().isNoContent());

        verify(portfolioService, times(1)).deletePortfolio(1L);
    }
}
