package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.PortfolioDTO;
import com.freelancenexus.freelancer.exception.ResourceNotFoundException;
import com.freelancenexus.freelancer.model.Freelancer;
import com.freelancenexus.freelancer.model.Portfolio;
import com.freelancenexus.freelancer.repository.FreelancerRepository;
import com.freelancenexus.freelancer.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PortfolioService.
 */
@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Captor
    private ArgumentCaptor<Portfolio> portfolioCaptor;

    private Freelancer freelancer;

    @BeforeEach
    void setUp() {
        freelancer = new Freelancer();
        freelancer.setId(10L);
        freelancer.setTitle("UI Dev");
    }

    // -------------------------
    // createPortfolio tests
    // -------------------------

    @Test
    void shouldCreatePortfolio_whenFreelancerExists() {
        // Arrange
        PortfolioDTO dto = new PortfolioDTO();
        dto.setFreelancerId(10L);
        dto.setTitle("E-Commerce App");
        dto.setDescription("Built a shop platform");
        dto.setProjectUrl("https://example.com");
        dto.setImageUrl("https://img.png");
        dto.setTechnologiesUsed("React,Spring");
        dto.setCompletionDate(LocalDate.now().minusDays(5));

        when(freelancerRepository.findById(10L)).thenReturn(Optional.of(freelancer));

        Portfolio saved = new Portfolio();
        saved.setId(1L);
        saved.setTitle("E-Commerce App");
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(saved);

        // Act
        PortfolioDTO result = portfolioService.createPortfolio(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("E-Commerce App", result.getTitle());

        verify(freelancerRepository).findById(10L);
        verify(portfolioRepository).save(portfolioCaptor.capture());

        Portfolio captured = portfolioCaptor.getValue();
        assertEquals("E-Commerce App", captured.getTitle());
        // confirm association was linked
        assertThat(freelancer.getPortfolios()).isNotNull();
    }

    @Test
    void shouldThrow_whenCreatePortfolioFreelancerNotFound() {
        when(freelancerRepository.findById(99L)).thenReturn(Optional.empty());
        PortfolioDTO dto = new PortfolioDTO();
        dto.setFreelancerId(99L);
        assertThrows(ResourceNotFoundException.class, () -> portfolioService.createPortfolio(dto));
        verify(portfolioRepository, never()).save(any());
    }

    // -------------------------
    // getPortfoliosByFreelancerId tests
    // -------------------------

    @Test
    void shouldReturnList_whenGetPortfoliosByFreelancerIdExists() {
        Portfolio p = new Portfolio();
        p.setId(2L);
        p.setTitle("Landing Page");
        when(portfolioRepository.findByFreelancerId(10L)).thenReturn(List.of(p));

        List<PortfolioDTO> result = portfolioService.getPortfoliosByFreelancerId(10L);

        assertEquals(1, result.size());
        assertEquals("Landing Page", result.get(0).getTitle());
        verify(portfolioRepository).findByFreelancerId(10L);
    }

    @Test
    void shouldReturnEmptyList_whenNoPortfoliosFound() {
        when(portfolioRepository.findByFreelancerId(15L)).thenReturn(List.of());
        List<PortfolioDTO> res = portfolioService.getPortfoliosByFreelancerId(15L);
        assertTrue(res.isEmpty());
    }

    // -------------------------
    // deletePortfolio tests
    // -------------------------

    @Test
    void shouldDeletePortfolio_whenExists() {
        Portfolio p = new Portfolio();
        p.setId(3L);
        when(portfolioRepository.findById(3L)).thenReturn(Optional.of(p));

        portfolioService.deletePortfolio(3L);

        verify(portfolioRepository).findById(3L);
        verify(portfolioRepository).delete(p);
    }

    @Test
    void shouldThrow_whenDeletePortfolioNotFound() {
        when(portfolioRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> portfolioService.deletePortfolio(404L));
        verify(portfolioRepository, never()).delete(any());
    }

    // -------------------------
    // mapping validation
    // -------------------------

    @Test
    void shouldMapEntityToDTOProperly() {
        Portfolio entity = new Portfolio();
        entity.setId(4L);
        entity.setTitle("Portfolio Mapping");
        entity.setDescription("desc");
        entity.setProjectUrl("url");
        entity.setImageUrl("img");
        entity.setTechnologiesUsed("Java");
        entity.setCompletionDate(LocalDate.now());
        entity.setCreatedAt(Instant.now());

        PortfolioDTO dto = portfolioService.toDTO(entity);

        assertEquals(4L, dto.getId());
        assertEquals("Portfolio Mapping", dto.getTitle());
        assertEquals("Java", dto.getTechnologiesUsed());
    }
}
