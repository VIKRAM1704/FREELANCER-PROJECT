package com.freelancenexus.userservice.dto;

import com.freelancenexus.userservice.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    private Long id;
    private String keycloakId;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    private Boolean isActive;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}