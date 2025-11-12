package com.freelancenexus.freelancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.freelancer.dto.FreelancerDTO;
import com.freelancenexus.freelancer.dto.FreelancerProfileDTO;
import com.freelancenexus.freelancer.service.FreelancerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FreelancerController.class)
class FreelancerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FreelancerService freelancerService;

    @Autowired
    private ObjectMapper objectMapper;

    private FreelancerDTO freelancerDTO;

    @BeforeEach
    void setUp() {
        freelancerDTO = new FreelancerDTO();
        freelancerDTO.setId(1L);
        freelancerDTO.setUserId(100L);
        freelancerDTO.setHourlyRate(BigDecimal.valueOf(50));
        freelancerDTO.setAvailability("Available");
    }

    @Test
    void shouldCreateFreelancer() throws Exception {
        when(freelancerService.createFreelancer(any(FreelancerDTO.class))).thenReturn(freelancerDTO);

        mockMvc.perform(post("/api/freelancers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(freelancerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(100L));
    }

    @Test
    void shouldGetFreelancerById() throws Exception {
        when(freelancerService.getFreelancerById(1L)).thenReturn(freelancerDTO);

        mockMvc.perform(get("/api/freelancers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(100L));
    }

    @Test
    void shouldGetFreelancerProfile() throws Exception {
        FreelancerProfileDTO profileDTO = new FreelancerProfileDTO();
        profileDTO.setFreelancer(freelancerDTO);

        when(freelancerService.getFreelancerProfile(1L)).thenReturn(profileDTO);

        mockMvc.perform(get("/api/freelancers/1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.freelancer.id").value(1L));
    }

    @Test
    void shouldGetFreelancerByUserId() throws Exception {
        when(freelancerService.getFreelancerByUserId(100L)).thenReturn(freelancerDTO);

        mockMvc.perform(get("/api/freelancers/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100L));
    }

    @Test
    void shouldUpdateFreelancer() throws Exception {
        freelancerDTO.setAvailability("Busy");
        when(freelancerService.updateFreelancer(eq(1L), any(FreelancerDTO.class))).thenReturn(freelancerDTO);

        mockMvc.perform(put("/api/freelancers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(freelancerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability").value("Busy"));
    }

    @Test
    void shouldReturnAllFreelancers() throws Exception {
        when(freelancerService.getAllFreelancers()).thenReturn(List.of(freelancerDTO));

        mockMvc.perform(get("/api/freelancers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldSearchFreelancersBySkills() throws Exception {
        when(freelancerService.getFreelancersBySkills(anyList())).thenReturn(List.of(freelancerDTO));

        mockMvc.perform(get("/api/freelancers")
                        .param("skills", "Java", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldSearchFreelancersByRateAndRating() throws Exception {
        when(freelancerService.searchFreelancers(any(), any(), any(), any()))
                .thenReturn(List.of(freelancerDTO));

        mockMvc.perform(get("/api/freelancers")
                        .param("minRate", "30")
                        .param("maxRate", "70")
                        .param("minRating", "4.5")
                        .param("availability", "Available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
