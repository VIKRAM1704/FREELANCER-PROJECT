package com.freelancenexus.freelancer.repository;

import com.freelancenexus.freelancer.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long>, JpaSpecificationExecutor<Freelancer> {
    
    Optional<Freelancer> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    @Query("SELECT f FROM Freelancer f LEFT JOIN FETCH f.skills WHERE f.id = :id")
    Optional<Freelancer> findByIdWithSkills(@Param("id") Long id);
    
    @Query("SELECT f FROM Freelancer f LEFT JOIN FETCH f.skills LEFT JOIN FETCH f.portfolios WHERE f.id = :id")
    Optional<Freelancer> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT DISTINCT f FROM Freelancer f JOIN f.skills s WHERE LOWER(s.skillName) IN :skills")
    List<Freelancer> findBySkills(@Param("skills") List<String> skills);
    
    List<Freelancer> findByAvailability(String availability);
    
    List<Freelancer> findByHourlyRateBetween(BigDecimal minRate, BigDecimal maxRate);
    
    List<Freelancer> findByAverageRatingGreaterThanEqual(BigDecimal minRating);
    
    @Query("SELECT f FROM Freelancer f WHERE " +
           "(:minRate IS NULL OR f.hourlyRate >= :minRate) AND " +
           "(:maxRate IS NULL OR f.hourlyRate <= :maxRate) AND " +
           "(:minRating IS NULL OR f.averageRating >= :minRating) AND " +
           "(:availability IS NULL OR f.availability = :availability)")
    List<Freelancer> searchFreelancers(
        @Param("minRate") BigDecimal minRate,
        @Param("maxRate") BigDecimal maxRate,
        @Param("minRating") BigDecimal minRating,
        @Param("availability") String availability
    );
}