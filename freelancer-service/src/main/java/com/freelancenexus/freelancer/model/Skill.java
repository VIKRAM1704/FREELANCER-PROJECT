package com.freelancenexus.freelancer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;
    
    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level", length = 50)
    private ProficiencyLevel proficiencyLevel;
    
    @Column(name = "years_experience")
    private Integer yearsExperience;
    
    public enum ProficiencyLevel {
        BEGINNER,
        INTERMEDIATE,
        EXPERT
    }
}