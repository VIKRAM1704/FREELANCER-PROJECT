package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.RatingDTO;
import com.freelancenexus.freelancer.exception.ResourceNotFoundException;
import com.freelancenexus.freelancer.model.Freelancer;
import com.freelancenexus.freelancer.model.Rating;
import com.freelancenexus.freelancer.repository.FreelancerRepository;
import com.freelancenexus.freelancer.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RatingService.
 */
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @InjectMocks
    private RatingService ratingService;

    @Captor
    private ArgumentCaptor<Rating> ratingCaptor;

    private Freelancer freelancer;

    @BeforeEach
    void init() {
        freelancer = new Freelancer();
        freelancer.setId(22L);
        freelancer.setTitle("DevOps Engineer");
    }

    // -------------------------
    // addRating tests
    // -------------------------

    @Test
    void shouldAddRating_whenFreelancerExists() {
        // Arrange
        RatingDTO dto = new RatingDTO();
        dto.setFreelancerId(22L);
        dto.setClientName("John Doe");
        dto.setScore(4.5);
        dto.setComment("Excellent work");

        when(freelancerRepository.findById(22L)).thenReturn(Optional.of(freelancer));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(inv -> {
            Rating saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        RatingDTO result = ratingService.addRating(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getClientName());
        assertEquals(4.5, result.getScore());
        verify(freelancerRepository).findById(22L);
        verify(ratingRepository).save(ratingCaptor.capture());

        Rating captured = ratingCaptor.getValue();
        assertEquals("Excellent work", captured.getComment());
        assertThat(freelancer.getRatings()).isNotNull();
    }

    @Test
    void shouldThrow_whenFreelancerNotFoundOnAddRating() {
        when(freelancerRepository.findById(100L)).thenReturn(Optional.empty());
        RatingDTO dto = new RatingDTO();
        dto.setFreelancerId(100L);
        dto.setScore(3.0);
        assertThrows(ResourceNotFoundException.class, () -> ratingService.addRating(dto));
        verify(ratingRepository, never()).save(any());
    }

    // -------------------------
    // getRatingsByFreelancerId tests
    // -------------------------

    @Test
    void shouldReturnRatings_whenFreelancerExists() {
        Rating r = new Rating();
        r.setId(10L);
        r.setClientName("Client A");
        r.setScore(5.0);
        r.setComment("Perfect!");
        r.setCreatedAt(Instant.now());

        when(ratingRepository.findByFreelancerId(22L)).thenReturn(List.of(r));

        List<RatingDTO> result = ratingService.getRatingsByFreelancerId(22L);

        assertEquals(1, result.size());
        assertEquals("Client A", result.get(0).getClientName());
        assertEquals(5.0, result.get(0).getScore());
        verify(ratingRepository).findByFreelancerId(22L);
    }

    @Test
    void shouldReturnEmptyList_whenNoRatingsFound() {
        when(ratingRepository.findByFreelancerId(33L)).thenReturn(List.of());
        List<RatingDTO> res = ratingService.getRatingsByFreelancerId(33L);
        assertTrue(res.isEmpty());
    }

    // -------------------------
    // internal mapping
    // -------------------------

    @Test
    void shouldMapEntityToDTOProperly() {
        Rating entity = new Rating();
        entity.setId(7L);
        entity.setClientName("Tester");
        entity.setScore(3.5);
        entity.setComment("Good enough");
        entity.setCreatedAt(Instant.now());

        RatingDTO dto = ratingService.toDTO(entity);

        assertEquals(7L, dto.getId());
        assertEquals("Tester", dto.getClientName());
        assertEquals(3.5, dto.getScore());
        assertEquals("Good enough", dto.getComment());
    }
}
