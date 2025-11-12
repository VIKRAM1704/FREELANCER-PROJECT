package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.FreelancerDTO;
import com.freelancenexus.freelancer.dto.FreelancerProfileDTO;
import com.freelancenexus.freelancer.dto.SkillDTO;
import com.freelancenexus.freelancer.exception.FreelancerAlreadyExistsException;
import com.freelancenexus.freelancer.exception.ResourceNotFoundException;
import com.freelancenexus.freelancer.model.Freelancer;
import com.freelancenexus.freelancer.model.Skill;
import com.freelancenexus.freelancer.repository.FreelancerRepository;
import com.freelancenexus.freelancer.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreelancerServiceTest {

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private FreelancerService freelancerService;

    private FreelancerDTO freelancerDTO;
    private Freelancer freelancer;

    @BeforeEach
    void setUp() {
        SkillDTO skillDTO = new SkillDTO(1L, "Java", null, 5);
        freelancerDTO = new FreelancerDTO(
                1L, 10L, "Java Developer", "Expert in Spring Boot",
                BigDecimal.valueOf(50), "AVAILABLE",
                BigDecimal.valueOf(1000), 5, BigDecimal.valueOf(4.5),
                List.of(skillDTO), null, null
        );

        freelancer = new Freelancer();
        freelancer.setId(1L);
        freelancer.setUserId(10L);
        freelancer.setTitle("Java Developer");
        freelancer.setBio("Expert in Spring Boot");
        freelancer.setHourlyRate(BigDecimal.valueOf(50));
        freelancer.setAvailability("AVAILABLE");
        freelancer.addSkill(new Skill());
    }

    @Test
    void shouldCreateFreelancer() {
        when(freelancerRepository.existsByUserId(freelancerDTO.getUserId())).thenReturn(false);
        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(freelancer);

        FreelancerDTO result = freelancerService.createFreelancer(freelancerDTO);

        assertNotNull(result);
        assertEquals(freelancer.getId(), result.getId());
        verify(freelancerRepository, times(1)).save(any(Freelancer.class));
    }

    @Test
    void shouldThrowExceptionWhenFreelancerAlreadyExists() {
        when(freelancerRepository.existsByUserId(freelancerDTO.getUserId())).thenReturn(true);

        assertThrows(FreelancerAlreadyExistsException.class,
                () -> freelancerService.createFreelancer(freelancerDTO));

        verify(freelancerRepository, never()).save(any(Freelancer.class));
    }

    @Test
    void shouldGetFreelancerById() {
        when(freelancerRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(freelancer));

        FreelancerDTO result = freelancerService.getFreelancerById(1L);

        assertNotNull(result);
        assertEquals(freelancer.getId(), result.getId());
        verify(freelancerRepository, times(1)).findByIdWithSkills(1L);
    }

    @Test
    void shouldThrowExceptionWhenFreelancerNotFoundById() {
        when(freelancerRepository.findByIdWithSkills(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> freelancerService.getFreelancerById(1L));
    }

    @Test
    void shouldGetFreelancerProfile() {
        when(freelancerRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(freelancer));

        FreelancerProfileDTO profile = freelancerService.getFreelancerProfile(1L);

        assertNotNull(profile);
        assertEquals(freelancer.getId(), profile.getId());
        verify(freelancerRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    void shouldThrowExceptionWhenFreelancerProfileNotFound() {
        when(freelancerRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> freelancerService.getFreelancerProfile(1L));
    }

    @Test
    void shouldGetFreelancerByUserId() {
        when(freelancerRepository.findByUserId(10L)).thenReturn(Optional.of(freelancer));

        FreelancerDTO result = freelancerService.getFreelancerByUserId(10L);

        assertNotNull(result);
        assertEquals(freelancer.getUserId(), result.getUserId());
    }

    @Test
    void shouldThrowExceptionWhenFreelancerNotFoundByUserId() {
        when(freelancerRepository.findByUserId(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> freelancerService.getFreelancerByUserId(10L));
    }

    @Test
    void shouldUpdateFreelancer() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.of(freelancer));
        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(freelancer);

        FreelancerDTO updatedDTO = freelancerService.updateFreelancer(1L, freelancerDTO);

        assertNotNull(updatedDTO);
        verify(freelancerRepository, times(1)).save(freelancer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingFreelancer() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> freelancerService.updateFreelancer(1L, freelancerDTO));
    }

    @Test
    void shouldSearchFreelancers() {
        when(freelancerRepository.searchFreelancers(any(), any(), any(), any())).thenReturn(List.of(freelancer));

        List<FreelancerDTO> results = freelancerService.searchFreelancers(BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, "AVAILABLE");

        assertFalse(results.isEmpty());
        verify(freelancerRepository, times(1)).searchFreelancers(any(), any(), any(), any());
    }

    @Test
    void shouldGetFreelancersBySkills() {
        when(freelancerRepository.findBySkills(anyList())).thenReturn(List.of(freelancer));

        List<FreelancerDTO> results = freelancerService.getFreelancersBySkills(List.of("Java"));

        assertFalse(results.isEmpty());
        verify(freelancerRepository, times(1)).findBySkills(anyList());
    }

    @Test
    void shouldGetAllFreelancers() {
        when(freelancerRepository.findAll()).thenReturn(List.of(freelancer));

        List<FreelancerDTO> results = freelancerService.getAllFreelancers();

        assertFalse(results.isEmpty());
        verify(freelancerRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateFreelancerStats() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.of(freelancer));
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(BigDecimal.valueOf(4.5));
        when(freelancerRepository.save(freelancer)).thenReturn(freelancer);

        freelancerService.updateFreelancerStats(1L);

        assertEquals(BigDecimal.valueOf(4.5), freelancer.getAverageRating());
        verify(freelancerRepository, times(1)).save(freelancer);
    }

    @Test
    void shouldSetAverageRatingToZeroIfNoRatings() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.of(freelancer));
        when(ratingRepository.calculateAverageRating(1L)).thenReturn(null);
        when(freelancerRepository.save(freelancer)).thenReturn(freelancer);

        freelancerService.updateFreelancerStats(1L);

        assertEquals(BigDecimal.ZERO, freelancer.getAverageRating());
    }
}
