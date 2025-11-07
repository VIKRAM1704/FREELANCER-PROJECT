package com.freelancenexus.userservice.service;

import com.freelancenexus.userservice.dto.*;
import com.freelancenexus.userservice.model.User;
import com.freelancenexus.userservice.repository.UserRepository;
import com.freelancenexus.userserviceservice.exception.DuplicateResourceException;
import com.freelancenexus.userserviceservice.exception.UnauthorizedException;
import com.freelancenexus.userserviceservice.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("User with email " + registrationDTO.getEmail() + " already exists");
        }
        
        // Create user in Keycloak
        String keycloakId = keycloakService.createKeycloakUser(registrationDTO);
        
        // Create user in local database
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(registrationDTO.getEmail());
        user.setFullName(registrationDTO.getFullName());
        user.setPhone(registrationDTO.getPhone());
        user.setRole(registrationDTO.getRole());
        user.setIsActive(true);
        user.setProfileImageUrl(registrationDTO.getProfileImageUrl());
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return mapToResponseDTO(savedUser);
    }
    
    @Transactional(readOnly = true)
    public LoginResponseDTO loginUser(UserLoginDTO loginDTO) {
        log.info("Authenticating user with email: {}", loginDTO.getEmail());
        
        // Authenticate with Keycloak
        LoginResponseDTO loginResponse = keycloakService.authenticateUser(loginDTO);
        
        // Get user from database
        User user = userRepository.findByEmail(loginDTO.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginDTO.getEmail()));
        
        // Set user info in response
        loginResponse.setUser(mapToResponseDTO(user));
        
        log.info("User authenticated successfully: {}", loginDTO.getEmail());
        return loginResponse;
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile() {
        String keycloakId = getCurrentUserKeycloakId();
        
        User user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return mapToResponseDTO(user);
    }
    
    @Transactional
    public UserResponseDTO updateCurrentUserProfile(UserUpdateDTO updateDTO) {
        String keycloakId = getCurrentUserKeycloakId();
        
        User user = userRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Update fields if provided
        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDTO.getProfileImageUrl());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully: {}", user.getId());
        
        return mapToResponseDTO(updatedUser);
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        return mapToResponseDTO(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Delete from Keycloak
        keycloakService.deleteKeycloakUser(user.getKeycloakId());
        
        // Delete from local database
        userRepository.delete(user);
        
        log.info("User deleted successfully: {}", id);
    }
    
    private String getCurrentUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        
        throw new UnauthorizedException("Invalid authentication token");
    }
    
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setKeycloakId(user.getKeycloakId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}