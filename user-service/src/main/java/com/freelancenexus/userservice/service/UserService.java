package com.freelancenexus.userservice.service;

import com.freelancenexus.userservice.dto.*;
import com.freelancenexus.userservice.model.User;
import com.freelancenexus.userservice.repository.UserRepository;
import com.freelancenexus.userservice.security.JwtTokenProvider;
import com.freelancenexus.userservice.exception.DuplicateResourceException;
import com.freelancenexus.userservice.exception.UnauthorizedException;
import com.freelancenexus.userservice.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("User with email " + registrationDTO.getEmail() + " already exists");
        }
        
        // Create user in database
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
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
        
        // Find user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        
        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is inactive");
        }
        
        // Verify password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
            user.getEmail(), 
            user.getId(), 
            user.getRole().name()
        );
        
        // Create response
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setAccessToken(token);
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(86400L); // 24 hours in seconds
        loginResponse.setUser(mapToResponseDTO(user));
        
        log.info("User authenticated successfully: {}", loginDTO.getEmail());
        return loginResponse;
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile() {
        Long userId = getCurrentUserId();
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return mapToResponseDTO(user);
    }
    
    @Transactional
    public UserResponseDTO updateCurrentUserProfile(UserUpdateDTO updateDTO) {
        Long userId = getCurrentUserId();
        
        User user = userRepository.findById(userId)
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
        
        userRepository.delete(user);
        log.info("User deleted successfully: {}", id);
    }
    
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof Long) {
            return (Long) principal;
        }
        
        throw new UnauthorizedException("User not authenticated");
    }
    
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
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