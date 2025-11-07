package com.freelancenexus.freelancer.repository;

import com.freelancenexus.freelancer.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    List<Rating> findByFreelancerId(Long freelancerId);
    
    List<Rating> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);
    
    Optional<Rating> findByFreelancerIdAndClientIdAndProjectId(Long freelancerId, Long clientId, Long projectId);
    
    boolean existsByFreelancerIdAndClientIdAndProjectId(Long freelancerId, Long clientId, Long projectId);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.freelancer.id = :freelancerId")
    BigDecimal calculateAverageRating(@Param("freelancerId") Long freelancerId);
    
    long countByFreelancerId(Long freelancerId);
}