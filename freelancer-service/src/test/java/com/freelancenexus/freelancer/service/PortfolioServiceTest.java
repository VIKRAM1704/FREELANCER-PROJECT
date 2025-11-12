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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Freelancer freelancer;
    private Portfolio portfolio;
    private PortfolioDTO portfolioDTO;

    @BeforeEach
    void setUp() {
        freelancer = new Freelancer();
        freelancer.setId(1L);

        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setTitle("Project 1");
        portfolio.setDescription("Description");
        portfolio.setProjectUrl("http://project.com");
        portfolio.setImageUrl("http://image.com");
        portfolio.setTechnologiesUsed("Java, Spring");
        portfolio.setCompletionDate(LocalDate.now());
        portfolio.setCreatedAt(LocalDateTime.now());

        portfolioDTO = new PortfolioDTO(
                1L, "Project 1", "Description", 
                "http://project.com", "http://image.com", 
                "Java, Spring", LocalDate.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldAddPortfolio() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.of(freelancer));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        PortfolioDTO result = portfolioService.addPortfolio(1L, portfolioDTO);

        assertNotNull(result);
        assertEquals(portfolio.getId(), result.getId());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    void shouldThrowExceptionWhenFreelancerNotFoundForAddPortfolio() {
        when(freelancerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.addPortfolio(1L, portfolioDTO));
        verify(portfolioRepository, never()).save(any(Portfolio.class));
    }

    @Test
    void shouldGetFreelancerPortfolios() {
        when(freelancerRepository.existsById(1L)).thenReturn(true);
        when(portfolioRepository.findByFreelancerId(1L)).thenReturn(List.of(portfolio));

        List<PortfolioDTO> results = portfolioService.getFreelancerPortfolios(1L);

        assertFalse(results.isEmpty());
        assertEquals(portfolio.getId(), results.get(0).getId());
    }

    @Test
    void shouldThrowExceptionWhenFreelancerNotFoundForGetPortfolios() {
        when(freelancerRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.getFreelancerPortfolios(1L));
    }

    @Test
    void shouldGetPortfolioById() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        PortfolioDTO result = portfolioService.getPortfolioById(1L);

        assertNotNull(result);
        assertEquals(portfolio.getId(), result.getId());
    }

    @Test
    void shouldThrowExceptionWhenPortfolioNotFoundById() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.getPortfolioById(1L));
    }

    @Test
    void shouldUpdatePortfolio() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        PortfolioDTO result = portfolioService.updatePortfolio(1L, portfolioDTO);

        assertNotNull(result);
        verify(portfolioRepository, times(1)).save(portfolio);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPortfolio() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.updatePortfolio(1L, portfolioDTO));
    }

    @Test
    void shouldDeletePortfolio() {
        when(portfolioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(portfolioRepository).deleteById(1L);

        assertDoesNotThrow(() -> portfolioService.deletePortfolio(1L));
        verify(portfolioRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingPortfolio() {
        when(portfolioRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.deletePortfolio(1L));
        verify(portfolioRepository, never()).deleteById(1L);
    }
}
