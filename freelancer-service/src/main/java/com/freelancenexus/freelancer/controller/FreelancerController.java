package com.freelancenexus.freelancer.controller;

import com.freelancenexus.freelancer.dto.FreelancerDTO;
import com.freelancenexus.freelancer.dto.FreelancerProfileDTO;
import com.freelancenexus.freelancer.service.FreelancerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/freelancers")
@RequiredArgsConstructor
@Slf4j
public class FreelancerController {
    
    private final FreelancerService freelancerService;
    
    // Only freelancers can create their profile
    @PostMapping
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<FreelancerDTO> createFreelancer(@Valid @RequestBody FreelancerDTO freelancerDTO) {
        log.info("REST request to create freelancer for user ID: {}", freelancerDTO.getUserId());
        FreelancerDTO created = freelancerService.createFreelancer(freelancerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Anyone authenticated can view freelancer profiles
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FreelancerDTO> getFreelancerById(@PathVariable Long id) {
        log.info("REST request to get freelancer by ID: {}", id);
        FreelancerDTO freelancer = freelancerService.getFreelancerById(id);
        return ResponseEntity.ok(freelancer);
    }
    
    @GetMapping("/{id}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FreelancerProfileDTO> getFreelancerProfile(@PathVariable Long id) {
        log.info("REST request to get complete freelancer profile for ID: {}", id);
        FreelancerProfileDTO profile = freelancerService.getFreelancerProfile(id);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FreelancerDTO> getFreelancerByUserId(@PathVariable Long userId) {
        log.info("REST request to get freelancer by user ID: {}", userId);
        FreelancerDTO freelancer = freelancerService.getFreelancerByUserId(userId);
        return ResponseEntity.ok(freelancer);
    }
    
    // Only the freelancer themselves can update their profile
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<FreelancerDTO> updateFreelancer(
            @PathVariable Long id,
            @Valid @RequestBody FreelancerDTO freelancerDTO) {
        log.info("REST request to update freelancer with ID: {}", id);
        FreelancerDTO updated = freelancerService.updateFreelancer(id, freelancerDTO);
        return ResponseEntity.ok(updated);
    }
    
    // Anyone authenticated can search freelancers
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FreelancerDTO>> searchFreelancers(
            @RequestParam(required = false) BigDecimal minRate,
            @RequestParam(required = false) BigDecimal maxRate,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) List<String> skills) {
        log.info("REST request to search freelancers");
        
        List<FreelancerDTO> freelancers;
        
        if (skills != null && !skills.isEmpty()) {
            freelancers = freelancerService.getFreelancersBySkills(skills);
        } else if (minRate != null || maxRate != null || minRating != null || availability != null) {
            freelancers = freelancerService.searchFreelancers(minRate, maxRate, minRating, availability);
        } else {
            freelancers = freelancerService.getAllFreelancers();
        }
        
        return ResponseEntity.ok(freelancers);
    }
}