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
import com.freelancenexus.userservice.model.UserRole;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    private UserRegistrationDTO registrationDTO;
    private User user;
    private UserLoginDTO loginDTO;
    private UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        registrationDTO = new UserRegistrationDTO(
                "test@example.com",
                "password123",
                "Test User",
                "1234567890",
                UserRole.CLIENT,
                null
        );

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFullName("Test User");
        user.setPhone("1234567890");
        user.setRole(UserRole.CLIENT);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        loginDTO = new UserLoginDTO("test@example.com", "password123");
        updateDTO = new UserUpdateDTO("Updated User", "0987654321", null);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        var response = userService.registerUser(registrationDTO);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository).existsByEmail(registrationDTO.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringDuplicateEmail() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> userService.registerUser(registrationDTO));

        verify(userRepository).existsByEmail(registrationDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUserSuccessfully() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name()))
                .thenReturn("jwt-token");

        var response = userService.loginUser(loginDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals(user.getEmail(), response.getUser().getEmail());
    }

    @Test
    void shouldThrowUnauthorizedIfUserNotFoundOnLogin() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDTO));
    }

    @Test
    void shouldThrowUnauthorizedIfPasswordDoesNotMatch() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDTO));
    }

    @Test
    void shouldThrowUnauthorizedIfUserInactive() {
        user.setIsActive(false);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDTO));
    }

    @Test
    void shouldGetCurrentUserProfileSuccessfully() {
        setAuthenticatedUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = userService.getCurrentUserProfile();

        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void shouldThrowUserNotFoundWhenGettingCurrentUserProfile() {
        setAuthenticatedUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUserProfile());
    }

    @Test
    void shouldUpdateCurrentUserProfileSuccessfully() {
        setAuthenticatedUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var response = userService.updateCurrentUserProfile(updateDTO);

        assertEquals(updateDTO.getFullName(), response.getFullName());
        assertEquals(updateDTO.getPhone(), response.getPhone());
    }

    @Test
    void shouldThrowUserNotFoundWhenUpdatingProfile() {
        setAuthenticatedUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateCurrentUserProfile(updateDTO));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = userService.getUserById(1L);

        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void shouldThrowUserNotFoundWhenGettingById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserResponseDTO> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(user.getEmail(), users.get(0).getEmail());
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowUserNotFoundWhenDeleting() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void shouldThrowUnauthorizedIfCurrentUserNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedException.class,
                () -> userService.getCurrentUserProfile());
    }

    private void setAuthenticatedUserId(Long userId) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getPrincipal()).thenReturn(userId);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
    }
}
