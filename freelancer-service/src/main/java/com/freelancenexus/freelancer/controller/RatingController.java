package com.freelancenexus.freelancer.controller;

import com.freelancenexus.freelancer.dto.RatingDTO;
import com.freelancenexus.freelancer.service.RatingService;
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
public class RatingController {
    
	private static final Logger log = LoggerFactory.getLogger(RatingController.class);
	
    private final RatingService ratingService;
    
    @PostMapping("/{id}/ratings")
    public ResponseEntity<RatingDTO> addRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingDTO ratingDTO) {
        log.info("REST request to add rating for freelancer ID: {}", id);
        RatingDTO created = ratingService.addRating(id, ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<RatingDTO>> getFreelancerRatings(@PathVariable Long id) {
        log.info("REST request to get ratings for freelancer ID: {}", id);
        List<RatingDTO> ratings = ratingService.getFreelancerRatings(id);
        return ResponseEntity.ok(ratings);
    }
}