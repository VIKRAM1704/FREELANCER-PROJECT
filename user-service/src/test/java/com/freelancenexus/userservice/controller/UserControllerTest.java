package com.freelancenexus.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.userservice.dto.*;
import com.freelancenexus.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private UserRegistrationDTO registrationDTO;
    private UserResponseDTO userResponseDTO;
    private UserLoginDTO loginDTO;
    private LoginResponseDTO loginResponseDTO;
    private UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        registrationDTO = new UserRegistrationDTO(
                "test@example.com",
                "password123",
                "Test User",
                "1234567890",
                UserRole.CLIENT,
                null
        );

        userResponseDTO = new UserResponseDTO(
                1L,
                "test@example.com",
                "Test User",
                "1234567890",
                UserRole.CLIENT,
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        loginDTO = new UserLoginDTO("test@example.com", "password123");

        loginResponseDTO = new LoginResponseDTO(
                "access-token",
                "refresh-token",
                3600L,
                "Bearer",
                userResponseDTO
        );

        updateDTO = new UserUpdateDTO("Updated User", "0987654321", null);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDTO.getId()))
                .andExpect(jsonPath("$.email").value(userResponseDTO.getEmail()));

        verify(userService, times(1)).registerUser(any(UserRegistrationDTO.class));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        when(userService.loginUser(any(UserLoginDTO.class))).thenReturn(loginResponseDTO);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.user.email").value(userResponseDTO.getEmail()));

        verify(userService, times(1)).loginUser(any(UserLoginDTO.class));
    }

    @Test
    @WithMockUser
    void shouldReturnCurrentUserProfile() throws Exception {
        when(userService.getCurrentUserProfile()).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponseDTO.getEmail()));

        verify(userService, times(1)).getCurrentUserProfile();
    }

    @Test
    @WithMockUser
    void shouldUpdateCurrentUserProfile() throws Exception {
        when(userService.updateCurrentUserProfile(any(UserUpdateDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponseDTO.getEmail()));

        verify(userService, times(1)).updateCurrentUserProfile(any(UserUpdateDTO.class));
    }

    @Test
    @WithMockUser
    void shouldReturnUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDTO.getId()));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllUsers() throws Exception {
        List<UserResponseDTO> users = Arrays.asList(userResponseDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void shouldReturnBadRequestForInvalidRegistration() throws Exception {
        UserRegistrationDTO invalidDTO = new UserRegistrationDTO("", "", "", "", null, null);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidLogin() throws Exception {
        UserLoginDTO invalidDTO = new UserLoginDTO("", "");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}
