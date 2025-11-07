package com.freelancenexus.freelancer.service;

import com.freelancenexus.freelancer.dto.RatingDTO;
import com.freelancenexus.freelancer.exception.DuplicateRatingException;
import com.freelancenexus.freelancer.exception.ResourceNotFoundException;
import com.freelancenexus.freelancer.model.Freelancer;
import com.freelancenexus.freelancer.model.Rating;
import com.freelancenexus.freelancer.repository.FreelancerRepository;
import com.freelancenexus.freelancer.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
	private static final Logger log = LoggerFactory.getLogger(RatingService.class);
	
    private final RatingRepository ratingRepository;
    private final FreelancerRepository freelancerRepository;
    private final FreelancerService freelancerService;
    
    @Transactional
    public RatingDTO addRating(Long freelancerId, RatingDTO ratingDTO) {
        log.info("Adding rating for freelancer ID: {} by client ID: {}", freelancerId, ratingDTO.getClientId());
        
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
            .orElseThrow(() -> new ResourceNotFoundException("Freelancer not found with ID: " + freelancerId));
        
        // Check if rating already exists for this project
        if (ratingDTO.getProjectId() != null && 
            ratingRepository.existsByFreelancerIdAndClientIdAndProjectId(
                freelancerId, ratingDTO.getClientId(), ratingDTO.getProjectId())) {
            throw new DuplicateRatingException("Rating already exists for this project");
        }
        
        Rating rating = mapToEntity(ratingDTO);
        freelancer.addRating(rating);
        
        Rating saved = ratingRepository.save(rating);
        log.info("Rating added with ID: {}", saved.getId());
        
        // Update freelancer's average rating
        freelancerService.updateFreelancerStats(freelancerId);
        
        return mapToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<RatingDTO> getFreelancerRatings(Long freelancerId) {
        log.info("Fetching ratings for freelancer ID: {}", freelancerId);
        
        if (!freelancerRepository.existsById(freelancerId)) {
            throw new ResourceNotFoundException("Freelancer not found with ID: " + freelancerId);
        }
        
        List<Rating> ratings = ratingRepository.findByFreelancerIdOrderByCreatedAtDesc(freelancerId);
        
        return ratings.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getAverageRating(Long freelancerId) {
        log.info("Calculating average rating for freelancer ID: {}", freelancerId);
        
        BigDecimal average = ratingRepository.calculateAverageRating(freelancerId);
        return average != null ? average : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public long getRatingCount(Long freelancerId) {
        return ratingRepository.countByFreelancerId(freelancerId);
    }
    
    private RatingDTO mapToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setClientId(rating.getClientId());
        dto.setProjectId(rating.getProjectId());
        dto.setRating(rating.getRating());
        dto.setReview(rating.getReview());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
    
    private Rating mapToEntity(RatingDTO dto) {
        Rating rating = new Rating();
        rating.setClientId(dto.getClientId());
        rating.setProjectId(dto.getProjectId());
        rating.setRating(dto.getRating());
        rating.setReview(dto.getReview());
        return rating;
    }
}