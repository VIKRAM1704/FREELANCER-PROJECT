package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.PortfolioDTO;
import com.freelancenexus.freelancer.exception.ResourceNotFoundException;
import com.freelancenexus.freelancer.model.Freelancer;
import com.freelancenexus.freelancer.model.Portfolio;
import com.freelancenexus.freelancer.repository.FreelancerRepository;
import com.freelancenexus.freelancer.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    
	private static final Logger log = LoggerFactory.getLogger(PortfolioService.class);
	
    private final PortfolioRepository portfolioRepository;
    private final FreelancerRepository freelancerRepository;
    
    @Transactional
    public PortfolioDTO addPortfolio(Long freelancerId, PortfolioDTO portfolioDTO) {
        log.info("Adding portfolio for freelancer ID: {}", freelancerId);
        
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
            .orElseThrow(() -> new ResourceNotFoundException("Freelancer not found with ID: " + freelancerId));
        
        Portfolio portfolio = mapToEntity(portfolioDTO);
        freelancer.addPortfolio(portfolio);
        
        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Portfolio added with ID: {}", saved.getId());
        
        return mapToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<PortfolioDTO> getFreelancerPortfolios(Long freelancerId) {
        log.info("Fetching portfolios for freelancer ID: {}", freelancerId);
        
        if (!freelancerRepository.existsById(freelancerId)) {
            throw new ResourceNotFoundException("Freelancer not found with ID: " + freelancerId);
        }
        
        List<Portfolio> portfolios = portfolioRepository.findByFreelancerId(freelancerId);
        
        return portfolios.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolioById(Long portfolioId) {
        log.info("Fetching portfolio by ID: {}", portfolioId);
        
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + portfolioId));
        
        return mapToDTO(portfolio);
    }
    
    @Transactional
    public PortfolioDTO updatePortfolio(Long portfolioId, PortfolioDTO portfolioDTO) {
        log.info("Updating portfolio with ID: {}", portfolioId);
        
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + portfolioId));
        
        if (portfolioDTO.getTitle() != null) {
            portfolio.setTitle(portfolioDTO.getTitle());
        }
        if (portfolioDTO.getDescription() != null) {
            portfolio.setDescription(portfolioDTO.getDescription());
        }
        if (portfolioDTO.getProjectUrl() != null) {
            portfolio.setProjectUrl(portfolioDTO.getProjectUrl());
        }
        if (portfolioDTO.getImageUrl() != null) {
            portfolio.setImageUrl(portfolioDTO.getImageUrl());
        }
        if (portfolioDTO.getTechnologiesUsed() != null) {
            portfolio.setTechnologiesUsed(portfolioDTO.getTechnologiesUsed());
        }
        if (portfolioDTO.getCompletionDate() != null) {
            portfolio.setCompletionDate(portfolioDTO.getCompletionDate());
        }
        
        Portfolio updated = portfolioRepository.save(portfolio);
        log.info("Portfolio updated successfully: {}", portfolioId);
        
        return mapToDTO(updated);
    }
    
    @Transactional
    public void deletePortfolio(Long portfolioId) {
        log.info("Deleting portfolio with ID: {}", portfolioId);
        
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new ResourceNotFoundException("Portfolio not found with ID: " + portfolioId);
        }
        
        portfolioRepository.deleteById(portfolioId);
        log.info("Portfolio deleted successfully: {}", portfolioId);
    }
    
    private PortfolioDTO mapToDTO(Portfolio portfolio) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(portfolio.getId());
        dto.setTitle(portfolio.getTitle());
        dto.setDescription(portfolio.getDescription());
        dto.setProjectUrl(portfolio.getProjectUrl());
        dto.setImageUrl(portfolio.getImageUrl());
        dto.setTechnologiesUsed(portfolio.getTechnologiesUsed());
        dto.setCompletionDate(portfolio.getCompletionDate());
        dto.setCreatedAt(portfolio.getCreatedAt());
        return dto;
    }
    
    private Portfolio mapToEntity(PortfolioDTO dto) {
        Portfolio portfolio = new Portfolio();
        portfolio.setTitle(dto.getTitle());
        portfolio.setDescription(dto.getDescription());
        portfolio.setProjectUrl(dto.getProjectUrl());
        portfolio.setImageUrl(dto.getImageUrl());
        portfolio.setTechnologiesUsed(dto.getTechnologiesUsed());
        portfolio.setCompletionDate(dto.getCompletionDate());
        return portfolio;
    }
}