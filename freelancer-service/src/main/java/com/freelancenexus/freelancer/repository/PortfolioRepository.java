package com.freelancenexus.freelancer.repository;

import com.freelancenexus.freelancer.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    List<Portfolio> findByFreelancerId(Long freelancerId);
    
    Optional<Portfolio> findByIdAndFreelancerId(Long id, Long freelancerId);
    
    void deleteByFreelancerId(Long freelancerId);
    
    long countByFreelancerId(Long freelancerId);
}