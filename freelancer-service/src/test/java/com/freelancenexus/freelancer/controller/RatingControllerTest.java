package com.freelancenexus.freelancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.freelancer.dto.RatingDTO;
import com.freelancenexus.freelancer.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private RatingDTO ratingDTO;

    @BeforeEach
    void setUp() {
        ratingDTO = new RatingDTO();
        ratingDTO.setId(1L);
        ratingDTO.setFreelancerId(100L);
        ratingDTO.setClientId(200L);
        ratingDTO.setScore(BigDecimal.valueOf(4.8));
        ratingDTO.setComment("Excellent work");
    }

    @Test
    void shouldAddRating() throws Exception {
        when(ratingService.addRating(eq(100L), any(RatingDTO.class))).thenReturn(ratingDTO);

        mockMvc.perform(post("/api/freelancers/100/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.freelancerId").value(100L))
                .andExpect(jsonPath("$.clientId").value(200L))
                .andExpect(jsonPath("$.score").value(4.8))
                .andExpect(jsonPath("$.comment").value("Excellent work"));
    }

    @Test
    void shouldGetFreelancerRatings() throws Exception {
        when(ratingService.getFreelancerRatings(100L)).thenReturn(List.of(ratingDTO));

        mockMvc.perform(get("/api/freelancers/100/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].freelancerId").value(100L))
                .andExpect(jsonPath("$[0].score").value(4.8))
                .andExpect(jsonPath("$[0].comment").value("Excellent work"));
    }
}
