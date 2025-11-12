package com.freelancenexus.userservice.service;

import com.freelancenexus.userservice.dto.*;
import com.freelancenexus.userservice.exception.DuplicateResourceException;
import com.freelancenexus.userservice.exception.UnauthorizedException;
import com.freelancenexus.userservice.exception.UserNotFoundException;
import com.freelancenexus.userservice.model.User;
import com.freelancenexus.userservice.repository.UserRepository;
import com.freelancenexus.userservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private SecurityContext securityContext;

    @InjectMocks private UserService userService;

    private User user;
    private UserRegistrationDTO registrationDTO;
    private UserLoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // Common test user
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFullName("Test User");
        user.setPhone("1234567890");
        user.setRole(UserRole.FREELANCER);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        registrationDTO = new UserRegistrationDTO(
                "test@example.com",
                "password123",
                "Test User",
                "1234567890",
                UserRole.FREELANCER,
                null
        );

        loginDTO = new UserLoginDTO("test@example.com", "password123");

        // Setup security context for current user
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(() -> 1L);
    }

    // ------------------------- REGISTER USER -------------------------
    @Test
    void shouldRegisterUser_whenEmailIsUnique() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.registerUser(registrationDTO);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(registrationDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    // ------------------------- LOGIN USER -------------------------
    @Test
    void shouldLoginUser_whenCredentialsAreValid() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name()))
                .thenReturn("token123");

        LoginResponseDTO response = userService.loginUser(loginDTO);

        assertNotNull(response);
        assertEquals("token123", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(user.getId(), response.getUser().getId());
    }

    @Test
    void shouldThrowUnauthorized_whenUserNotFound() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDTO));
    }

    @Test
    void shouldThrowUnauthorized_whenPasswordInvalid() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDTO));
    }

    @Test
    void shouldThrowUnauthorized_whenUserInactive() {
        user.setIsActive(false);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDTO));
    }

    // ------------------------- GET CURRENT USER PROFILE -------------------------
    @Test
    void shouldReturnCurrentUserProfile_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getCurrentUserProfile();

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void shouldThrowUserNotFound_whenCurrentUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUserProfile());
    }

    // ------------------------- UPDATE CURRENT USER PROFILE -------------------------
    @Test
    void shouldUpdateCurrentUserProfile_whenDataProvided() {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", "9999999999", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO response = userService.updateCurrentUserProfile(updateDTO);

        assertEquals("Updated Name", response.getFullName());
        assertEquals("9999999999", response.getPhone());
    }

    @Test
    void shouldThrowUserNotFound_whenUpdateFails() {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateCurrentUserProfile(updateDTO));
    }

    // ------------------------- GET USER BY ID -------------------------
    @Test
    void shouldReturnUser_whenExistsById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(1L);

        assertEquals(user.getId(), response.getId());
    }

    @Test
    void shouldThrowUserNotFound_whenUserDoesNotExistById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    // ------------------------- GET ALL USERS -------------------------
    @Test
    void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertEquals(user.getId(), users.get(0).getId());
    }

    // ------------------------- DELETE USER -------------------------
    @Test
    void shouldDeleteUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void shouldThrowUserNotFound_whenDeleteFails() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }
}
