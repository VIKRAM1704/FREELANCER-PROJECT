package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.FreelancerDTO;
import com.freelancenexus.freelancer.dto.FreelancerProfileDTO;
import com.freelancenexus.freelancer.dto.SkillDTO;
import com.freelancenexus.freelancer.exception.FreelancerAlreadyExistsException;
import com.freelancenexus.freelancer.exception.ResourceNotFoundException;
import com.freelancenexus.freelancer.model.Freelancer;
import com.freelancenexus.freelancer.model.Portfolio;
import com.freelancenexus.freelancer.model.Rating;
import com.freelancenexus.freelancer.model.Skill;
import com.freelancenexus.freelancer.repository.FreelancerRepository;
import com.freelancenexus.freelancer.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FreelancerService.
 *
 * - Uses Mockito to mock repositories.
 * - Covers success + failure cases for public methods.
 * - Verifies repository interactions and mapping logic.
 */
@ExtendWith(MockitoExtension.class)
class FreelancerServiceTest {

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private FreelancerService freelancerService;

    @Captor
    private ArgumentCaptor<List<String>> skillsListCaptor;

    @BeforeEach
    void setUp() {
        // Mockito annotations are initialized by the extension; nothing extra required here.
    }

    // -------------------------
    // createFreelancer tests
    // -------------------------

    @Test
    void shouldCreateFreelancer_whenUserDoesNotExist() {
        // Arrange
        FreelancerDTO request = new FreelancerDTO();
        request.setUserId(100L);
        request.setTitle("Backend Dev");
        SkillDTO skillDto = new SkillDTO();
        skillDto.setSkillName("Java");
        skillDto.setProficiencyLevel(null); // service will copy fields; level not required for this test
        request.setSkills(List.of(skillDto));

        when(freelancerRepository.existsByUserId(100L)).thenReturn(false);

        Freelancer saved = new Freelancer();
        saved.setId(1L);
        saved.setUserId(100L);
        saved.setTitle("Backend Dev");
        saved.setCreatedAt(Instant.now());

        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(saved);

        // Act
        FreelancerDTO result = freelancerService.createFreelancer(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getUserId());
        assertEquals("Backend Dev", result.getTitle());
        verify(freelancerRepository).existsByUserId(100L);
        verify(freelancerRepository).save(any(Freelancer.class));
    }

    @Test
    void shouldThrow_whenCreateFreelancer_ifUserAlreadyHasFreelancer() {
        // Arrange
        FreelancerDTO request = new FreelancerDTO();
        request.setUserId(200L);

        when(freelancerRepository.existsByUserId(200L)).thenReturn(true);

        // Act / Assert
        assertThrows(FreelancerAlreadyExistsException.class, () -> freelancerService.createFreelancer(request));
        verify(freelancerRepository).existsByUserId(200L);
        verify(freelancerRepository, never()).save(any());
    }

    // -------------------------
    // getFreelancerById tests
    // -------------------------

    @Test
    void shouldReturnFreelancerDTO_whenGetFreelancerByIdExists() {
        // Arrange
        Freelancer f = new Freelancer();
        f.setId(10L);
        f.setUserId(1000L);
        f.setTitle("Fullstack");
        f.setCreatedAt(Instant.now());

        when(freelancerRepository.findByIdWithSkills(10L)).thenReturn(Optional.of(f));

        // Act
        FreelancerDTO dto = freelancerService.getFreelancerById(10L);

        // Assert
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(1000L, dto.getUserId());
        assertEquals("Fullstack", dto.getTitle());
        verify(freelancerRepository).findByIdWithSkills(10L);
    }

    @Test
    void shouldThrow_whenGetFreelancerById_notFound() {
        when(freelancerRepository.findByIdWithSkills(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> freelancerService.getFreelancerById(999L));
        verify(freelancerRepository).findByIdWithSkills(999L);
    }

    // -------------------------
    // getFreelancerProfile tests
    // -------------------------

    @Test
    void shouldReturnProfileDTO_whenGetFreelancerProfileExists_withPortfoliosAndRatings() {
        // Arrange
        Freelancer f = new Freelancer();
        f.setId(20L);
        f.setUserId(2000L);
        f.setTitle("Designer");
        f.setCreatedAt(Instant.now());
        f.setUpdatedAt(Instant.now());

        // Add a portfolio to the freelancer model (service maps portfolios to DTOs)
        Portfolio p = new Portfolio();
        p.setId(300L);
        p.setTitle("Site Redesign");
        p.setDescription("A sample project");
        p.setProjectUrl("https://example.com");
        p.setImageUrl("https://example.com/img.png");
        p.setTechnologiesUsed("React");
        p.setCompletionDate(LocalDate.now().minusDays(10));
        p.setCreatedAt(Instant.now());

        // Add a rating to the freelancer model (service maps recentRatings)
        Rating r = new Rating();
        r.setId(400L);
        r.setClientId(77L);
        r.setProjectId(88L);
        r.setRating(5);
        r.setReview("Great work");
        r.setCreatedAt(Instant.now());

        // Set collections - assume getters accept these types
        f.setPortfolios(new ArrayList<>(List.of(p)));
        f.setRatings(new ArrayList<>(List.of(r)));

        when(freelancerRepository.findByIdWithDetails(20L)).thenReturn(Optional.of(f));

        // Act
        FreelancerProfileDTO profile = freelancerService.getFreelancerProfile(20L);

        // Assert
        assertNotNull(profile);
        assertEquals(20L, profile.getId());
        assertThat(profile.getPortfolios()).isNotEmpty();
        assertThat(profile.getRecentRatings()).isNotEmpty();
        assertEquals(300L, profile.getPortfolios().get(0).getId());
        assertEquals(400L, profile.getRecentRatings().get(0).getId());
        verify(freelancerRepository).findByIdWithDetails(20L);
    }

    @Test
    void shouldThrow_whenGetFreelancerProfile_notFound() {
        when(freelancerRepository.findByIdWithDetails(555L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> freelancerService.getFreelancerProfile(555L));
        verify(freelancerRepository).findByIdWithDetails(555L);
    }

    // -------------------------
    // getFreelancerByUserId tests
    // -------------------------

    @Test
    void shouldReturnDTO_whenGetFreelancerByUserIdExists() {
        Freelancer f = new Freelancer();
        f.setId(30L);
        f.setUserId(3000L);
        f.setTitle("DevOps");

        when(freelancerRepository.findByUserId(3000L)).thenReturn(Optional.of(f));

        FreelancerDTO dto = freelancerService.getFreelancerByUserId(3000L);

        assertNotNull(dto);
        assertEquals(30L, dto.getId());
        assertEquals(3000L, dto.getUserId());
        verify(freelancerRepository).findByUserId(3000L);
    }

    @Test
    void shouldThrow_whenGetFreelancerByUserId_notFound() {
        when(freelancerRepository.findByUserId(9999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> freelancerService.getFreelancerByUserId(9999L));
        verify(freelancerRepository).findByUserId(9999L);
    }

    // -------------------------
    // updateFreelancer tests
    // -------------------------

    @Test
    void shouldUpdateFreelancer_whenFieldsProvided() {
        // Arrange existing freelancer
        Freelancer existing = new Freelancer();
        existing.setId(40L);
        existing.setTitle("Old Title");
        existing.setBio("old bio");
        existing.setHourlyRate(new BigDecimal("10"));
        existing.setAvailability("AVAILABLE");
        existing.setSkills(new HashSet<>());

        when(freelancerRepository.findById(40L)).thenReturn(Optional.of(existing));
        when(freelancerRepository.save(any(Freelancer.class))).thenAnswer(inv -> inv.getArgument(0));

        // New DTO with updates
        FreelancerDTO updateDto = new FreelancerDTO();
        updateDto.setTitle("New Title");
        updateDto.setBio("new bio");
        updateDto.setHourlyRate(new BigDecimal("55.50"));
        updateDto.setAvailability("BUSY");
        SkillDTO s = new SkillDTO();
        s.setSkillName("Spring");
        s.setYearsExperience(3);
        updateDto.setSkills(List.of(s));

        // Act
        FreelancerDTO result = freelancerService.updateFreelancer(40L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("new bio", result.getBio());
        assertEquals(new BigDecimal("55.50"), result.getHourlyRate());
        assertEquals("BUSY", result.getAvailability());
        verify(freelancerRepository).findById(40L);
        verify(freelancerRepository).save(existing);
    }

    @Test
    void shouldThrow_whenUpdateFreelancer_notFound() {
        when(freelancerRepository.findById(12345L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> freelancerService.updateFreelancer(12345L, new FreelancerDTO()));
        verify(freelancerRepository).findById(12345L);
    }

    // -------------------------
    // searchFreelancers tests
    // -------------------------

    @Test
    void shouldReturnListOfDTOs_whenSearchFreelancersDelegatesToRepository() {
        Freelancer f = new Freelancer();
        f.setId(50L);
        when(freelancerRepository.searchFreelancers(any(), any(), any(), anyString())).thenReturn(List.of(f));

        var results = freelancerService.searchFreelancers(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("4"), "AVAILABLE");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(50L, results.get(0).getId());
        verify(freelancerRepository).searchFreelancers(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("4"), "AVAILABLE");
    }

    // -------------------------
    // getFreelancersBySkills tests
    // -------------------------

    @Test
    void shouldLowercaseSkillsAndReturnResults_whenGetFreelancersBySkillsCalled() {
        Freelancer f = new Freelancer();
        f.setId(60L);

        when(freelancerRepository.findBySkills(anyList())).thenReturn(List.of(f));

        var res = freelancerService.getFreelancersBySkills(List.of("JaVa", "SPRING"));

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(60L, res.get(0).getId());

        // Verify that repository was called with lowercase skills
        verify(freelancerRepository).findBySkills(skillsListCaptor.capture());
        List<String> captured = skillsListCaptor.getValue();
        assertThat(captured).containsExactlyInAnyOrder("java", "spring");
    }

    // -------------------------
    // getAllFreelancers tests
    // -------------------------

    @Test
    void shouldReturnAllFreelancersMappedToDTOs_whenGetAllFreelancers() {
        Freelancer f = new Freelancer();
        f.setId(70L);
        when(freelancerRepository.findAll()).thenReturn(List.of(f));

        var list = freelancerService.getAllFreelancers();

        assertEquals(1, list.size());
        assertEquals(70L, list.get(0).getId());
        verify(freelancerRepository).findAll();
    }

    // -------------------------
    // updateFreelancerStats tests
    // -------------------------

    @Test
    void shouldUpdateAverageRating_whenRatingExists() {
        Freelancer f = new Freelancer();
        f.setId(80L);
        f.setAverageRating(BigDecimal.ZERO);

        when(freelancerRepository.findById(80L)).thenReturn(Optional.of(f));
        when(ratingRepository.calculateAverageRating(80L)).thenReturn(new BigDecimal("4.2"));
        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(f);

        freelancerService.updateFreelancerStats(80L);

        assertEquals(new BigDecimal("4.2"), f.getAverageRating());
        verify(freelancerRepository).findById(80L);
        verify(ratingRepository).calculateAverageRating(80L);
        verify(freelancerRepository).save(f);
    }

    @Test
    void shouldSetZeroAverageRating_whenRepositoryReturnsNull() {
        Freelancer f = new Freelancer();
        f.setId(81L);
        f.setAverageRating(new BigDecimal("3.3"));

        when(freelancerRepository.findById(81L)).thenReturn(Optional.of(f));
        when(ratingRepository.calculateAverageRating(81L)).thenReturn(null);
        when(freelancerRepository.save(any(Freelancer.class))).thenReturn(f);

        freelancerService.updateFreelancerStats(81L);

        assertEquals(BigDecimal.ZERO, f.getAverageRating());
        verify(ratingRepository).calculateAverageRating(81L);
        verify(freelancerRepository).save(f);
    }

    @Test
    void shouldThrow_whenUpdateFreelancerStatsFreelancerNotFound() {
        when(freelancerRepository.findById(99999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> freelancerService.updateFreelancerStats(99999L));
        verify(freelancerRepository).findById(99999L);
        verifyNoMoreInteractions(ratingRepository);
    }
}
