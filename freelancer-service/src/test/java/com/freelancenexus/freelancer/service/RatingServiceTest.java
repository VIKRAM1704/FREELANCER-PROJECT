package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.RatingDTO;
import com.freelancenexus.freelancer.exception.DuplicateRatingException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private FreelancerService freelancerService;

    @InjectMocks
    private RatingService ratingService;

    private Freelancer freelancer;
    private Rating rating;
    private RatingDTO ratingDTO;

    @BeforeEach
    void setUp() {
        freelancer = new Freelancer();
        freelancer.setId(1L);

        rating = new Rating();
        rating.setId(1L);
        rating.setClientId(10L);
        rating.setProjectId(100L);
        rating.setRating(5);
        rating.setReview("Excellent work");
        rating.setCreatedAt(LocalDateTime.now());

        ratingDTO = new RatingDTO(1L, 10L, 100L, 5, "Excellent work", LocalDateTime.now());
    }

    @Test
    void shouldAddRatingSuccessfully() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.of(freelancer));
        when(ratingRepository.existsByFreelancerIdAndClientIdAndProjectId(1L, 10L, 100L)).thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        RatingDTO result = ratingService.addRating(1L, ratingDTO);

        assertNotNull(result);
        assertEquals(rating.getId(), result.getId());
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(freelancerService, times(1)).updateFreelancerStats(1L);
    }

    @Test
    void shouldThrowExceptionWhenFreelancerNotFoundForAddRating() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ratingService.addRating(1L, ratingDTO));
        verify(ratingRepository, never()).save(any());
        verify(freelancerService, never()).updateFreelancerStats(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenDuplicateRating() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.of(freelancer));
        when(ratingRepository.existsByFreelancerIdAndClientIdAndProjectId(1L, 10L, 100L)).thenReturn(true);

        assertThrows(DuplicateRatingException.class, () -> ratingService.addRating(1L, ratingDTO));
        verify(ratingRepository, never()).save(any());
        verify(freelancerService, never()).updateFreelancerStats(anyLong());
    }

    @Test
    void shouldGetFreelancerRatings() {
        when(freelancerRepository.existsById(1L)).thenReturn(true);
        when(ratingRepository.findByFreelancerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(rating));

        List<RatingDTO> results = ratingService.getFreelancerRatings(1L);

        assertFalse(results.isEmpty());
        assertEquals(rating.getId(), results.get(0).getId());
    }

    @Test
    void shouldThrowExceptionWhenFreelancerNotFoundForGetRatings() {
        when(freelancerRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ratingService.getFreelancerRatings(1L));
    }

    @Test
    void shouldReturnAverageRating() {
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(new BigDecimal("4.5"));

        BigDecimal avg = ratingService.getAverageRating(1L);

        assertEquals(new BigDecimal("4.5"), avg);
    }

    @Test
    void shouldReturnZeroWhenNoAverageRating() {
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(null);

        BigDecimal avg = ratingService.getAverageRating(1L);

        assertEquals(BigDecimal.ZERO, avg);
    }

    @Test
    void shouldReturnRatingCount() {
        when(ratingRepository.countByFreelancerId(1L)).thenReturn(3L);

        long count = ratingService.getRatingCount(1L);

        assertEquals(3L, count);
    }
}
