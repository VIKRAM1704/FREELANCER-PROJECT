package com.freelancenexus.freelancer.controller;

import com.freelancenexus.freelancer.dto.PortfolioDTO;
import com.freelancenexus.freelancer.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;
    
    @PostMapping("/{id}/portfolio")
    public ResponseEntity<PortfolioDTO> addPortfolio(
            @PathVariable Long id,
            @Valid @RequestBody PortfolioDTO portfolioDTO) {
        log.info("REST request to add portfolio for freelancer ID: {}", id);
        PortfolioDTO created = portfolioService.addPortfolio(id, portfolioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}/portfolio")
    public ResponseEntity<List<PortfolioDTO>> getFreelancerPortfolios(@PathVariable Long id) {
        log.info("REST request to get portfolios for freelancer ID: {}", id);
        List<PortfolioDTO> portfolios = portfolioService.getFreelancerPortfolios(id);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long portfolioId) {
        log.info("REST request to get portfolio by ID: {}", portfolioId);
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }
    
    @PutMapping("/portfolio/{portfolioId}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
            @PathVariable Long portfolioId,
            @Valid @RequestBody PortfolioDTO portfolioDTO) {
        log.info("REST request to update portfolio with ID: {}", portfolioId);
        PortfolioDTO updated = portfolioService.updatePortfolio(portfolioId, portfolioDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/portfolio/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long portfolioId) {
        log.info("REST request to delete portfolio with ID: {}", portfolioId);
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }
}