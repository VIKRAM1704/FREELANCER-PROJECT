package com.freelancenexus.freelancer.controller;

import com.freelancenexus.freelancer.dto.PortfolioDTO;
import com.freelancenexus.freelancer.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;
    
    // Only freelancers can add portfolio items
    @PostMapping("/{id}/portfolio")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<PortfolioDTO> addPortfolio(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioDTO portfolioDTO) {
        log.info("REST request to add portfolio for freelancer ID: {}", id);
        PortfolioDTO created = portfolioService.addPortfolio(id, portfolioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Anyone authenticated can view portfolios
    @GetMapping("/{id}/portfolio")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PortfolioDTO>> getFreelancerPortfolios(@PathVariable Long id) {
        log.info("REST request to get portfolios for freelancer ID: {}", id);
        List<PortfolioDTO> portfolios = portfolioService.getFreelancerPortfolios(id);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/portfolio/{portfolioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long portfolioId) {
        log.info("REST request to get portfolio by ID: {}", portfolioId);
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }
    
    // Only freelancers can update their portfolio
    @PutMapping("/portfolio/{portfolioId}")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
            @PathVariable Long portfolioId,
            @Valid @RequestBody PortfolioDTO portfolioDTO) {
        log.info("REST request to update portfolio with ID: {}", portfolioId);
        PortfolioDTO updated = portfolioService.updatePortfolio(portfolioId, portfolioDTO);
        return ResponseEntity.ok(updated);
    }
    
    // Only freelancers can delete their portfolio
    @DeleteMapping("/portfolio/{portfolioId}")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long portfolioId) {
        log.info("REST request to delete portfolio with ID: {}", portfolioId);
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }
}