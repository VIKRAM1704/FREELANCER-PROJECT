package com.freelancenexus.freelancer.dto;

import com.freelancenexus.freelancer.model.Skill.ProficiencyLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    
    private Long id;
    
    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    private String skillName;
    
    @NotNull(message = "Proficiency level is required")
    private ProficiencyLevel proficiencyLevel;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience seems too high")
    private Integer yearsExperience;
}