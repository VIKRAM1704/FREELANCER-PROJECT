package com.freelancenexus.freelancer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.freelancer.dto.RatingDTO;
import com.freelancenexus.freelancer.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private RatingDTO ratingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();
        objectMapper = new ObjectMapper();

        ratingDTO = new RatingDTO(
                1L,
                20L,
                100L,
                5,
                "Excellent work",
                LocalDateTime.now()
        );
    }

    @Test
    void shouldAddRating() throws Exception {
        when(ratingService.addRating(eq(1L), any(RatingDTO.class))).thenReturn(ratingDTO);

        mockMvc.perform(post("/api/freelancers/1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ratingDTO.getId()))
                .andExpect(jsonPath("$.rating").value(ratingDTO.getRating()))
                .andExpect(jsonPath("$.review").value(ratingDTO.getReview()));

        verify(ratingService, times(1)).addRating(eq(1L), any(RatingDTO.class));
    }

    @Test
    void shouldGetFreelancerRatings() throws Exception {
        when(ratingService.getFreelancerRatings(1L)).thenReturn(List.of(ratingDTO));

        mockMvc.perform(get("/api/freelancers/1/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ratingDTO.getId()))
                .andExpect(jsonPath("$[0].rating").value(ratingDTO.getRating()))
                .andExpect(jsonPath("$[0].review").value(ratingDTO.getReview()));

        verify(ratingService, times(1)).getFreelancerRatings(1L);
    }
}
