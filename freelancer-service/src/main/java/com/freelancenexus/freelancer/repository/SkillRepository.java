package com.freelancenexus.freelancer.repository;

import com.freelancenexus.freelancer.model.Skill;
import com.freelancenexus.freelancer.model.Skill.ProficiencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    List<Skill> findByFreelancerId(Long freelancerId);
    
    List<Skill> findByFreelancerIdAndProficiencyLevel(Long freelancerId, ProficiencyLevel level);
    
    boolean existsByFreelancerIdAndSkillNameIgnoreCase(Long freelancerId, String skillName);
    
    void deleteByFreelancerId(Long freelancerId);
}