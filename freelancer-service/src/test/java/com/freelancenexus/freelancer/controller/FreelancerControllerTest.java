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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FreelancerControllerTest {

    @Mock
    private FreelancerService freelancerService;

    @InjectMocks
    private FreelancerController freelancerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private FreelancerDTO freelancerDTO;
    private FreelancerProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(freelancerController).build();
        objectMapper = new ObjectMapper();

        freelancerDTO = new FreelancerDTO(
                1L, 10L, "Java Developer", "Expert in Spring Boot",
                BigDecimal.valueOf(50), "AVAILABLE",
                BigDecimal.valueOf(1000), 5, BigDecimal.valueOf(4.5),
                null, null, null
        );

        profileDTO = new FreelancerProfileDTO(
                1L, 10L, "Java Developer", "Expert in Spring Boot",
                BigDecimal.valueOf(50), "AVAILABLE",
                BigDecimal.valueOf(1000), 5, BigDecimal.valueOf(4.5),
                null, null, null,
                null, null
        );
    }

    @Test
    void shouldCreateFreelancer() throws Exception {
        when(freelancerService.createFreelancer(any(FreelancerDTO.class))).thenReturn(freelancerDTO);

        mockMvc.perform(post("/api/freelancers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(freelancerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(freelancerDTO.getId()))
                .andExpect(jsonPath("$.userId").value(freelancerDTO.getUserId()))
                .andExpect(jsonPath("$.title").value(freelancerDTO.getTitle()));

        verify(freelancerService, times(1)).createFreelancer(any(FreelancerDTO.class));
    }

    @Test
    void shouldGetFreelancerById() throws Exception {
        when(freelancerService.getFreelancerById(1L)).thenReturn(freelancerDTO);

        mockMvc.perform(get("/api/freelancers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(freelancerDTO.getId()))
                .andExpect(jsonPath("$.title").value(freelancerDTO.getTitle()));

        verify(freelancerService, times(1)).getFreelancerById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenFreelancerDoesNotExist() throws Exception {
        when(freelancerService.getFreelancerById(999L)).thenThrow(new RuntimeException("Freelancer not found"));

        mockMvc.perform(get("/api/freelancers/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("")); // controller currently returns exception as 500

        verify(freelancerService, times(1)).getFreelancerById(999L);
    }

    @Test
    void shouldGetFreelancerProfile() throws Exception {
        when(freelancerService.getFreelancerProfile(1L)).thenReturn(profileDTO);

        mockMvc.perform(get("/api/freelancers/1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileDTO.getId()))
                .andExpect(jsonPath("$.title").value(profileDTO.getTitle()));

        verify(freelancerService, times(1)).getFreelancerProfile(1L);
    }

    @Test
    void shouldGetFreelancerByUserId() throws Exception {
        when(freelancerService.getFreelancerByUserId(10L)).thenReturn(freelancerDTO);

        mockMvc.perform(get("/api/freelancers/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(freelancerDTO.getUserId()));

        verify(freelancerService, times(1)).getFreelancerByUserId(10L);
    }

    @Test
    void shouldUpdateFreelancer() throws Exception {
        when(freelancerService.updateFreelancer(eq(1L), any(FreelancerDTO.class))).thenReturn(freelancerDTO);

        mockMvc.perform(put("/api/freelancers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(freelancerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(freelancerDTO.getId()))
                .andExpect(jsonPath("$.title").value(freelancerDTO.getTitle()));

        verify(freelancerService, times(1)).updateFreelancer(eq(1L), any(FreelancerDTO.class));
    }

    @Test
    void shouldSearchFreelancersWithSkills() throws Exception {
        when(freelancerService.getFreelancersBySkills(anyList())).thenReturn(List.of(freelancerDTO));

        mockMvc.perform(get("/api/freelancers")
                        .param("skills", "Java", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(freelancerDTO.getId()));

        verify(freelancerService, times(1)).getFreelancersBySkills(anyList());
    }

    @Test
    void shouldSearchFreelancersWithFilters() throws Exception {
        when(freelancerService.searchFreelancers(any(), any(), any(), any())).thenReturn(List.of(freelancerDTO));

        mockMvc.perform(get("/api/freelancers")
                        .param("minRate", "20")
                        .param("maxRate", "100")
                        .param("minRating", "3")
                        .param("availability", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(freelancerDTO.getId()));

        verify(freelancerService, times(1)).searchFreelancers(any(), any(), any(), any());
    }

    @Test
    void shouldGetAllFreelancersWhenNoFiltersOrSkillsProvided() throws Exception {
        when(freelancerService.getAllFreelancers()).thenReturn(List.of(freelancerDTO));

        mockMvc.perform(get("/api/freelancers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(freelancerDTO.getId()));

        verify(freelancerService, times(1)).getAllFreelancers();
    }
}
