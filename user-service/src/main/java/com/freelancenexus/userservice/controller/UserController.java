package com.freelancenexus.userservice.controller;

import com.freelancenexus.userservice.dto.*;
import com.freelancenexus.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Optional: Backup CORS support
@Slf4j
@RequiredArgsConstructor // This generates constructor for final fields
public class UserController {
    
    @Autowired  
    public UserController(UserService userService) {
        this.userService = userService;
    }
	
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        log.info("Received registration request for email: {}", registrationDTO.getEmail());
        UserResponseDTO response = userService.registerUser(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        log.info("Received login request for email: {}", loginDTO.getEmail());
        LoginResponseDTO response = userService.loginUser(loginDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        log.info("Received request to get current user profile");
        UserResponseDTO response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("Received request to update current user profile");
        UserResponseDTO response = userService.updateCurrentUserProfile(updateDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_FREELANCER')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Received request to get user by ID: {}", id);
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Received request to get all users");
        List<UserResponseDTO> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Received request to delete user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}