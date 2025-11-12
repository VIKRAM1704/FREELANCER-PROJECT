package com.freelancenexus.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.userservice.dto.*;
import com.freelancenexus.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        userResponseDTO = new UserResponseDTO(
                1L,
                "test@example.com",
                "Test User",
                "1234567890",
                UserRole.FREELANCER,
                true,
                null,
                null,
                null
        );
    }

    // ------------------------- REGISTER USER -------------------------
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "test@example.com",
                "password123",
                "Test User",
                "1234567890",
                UserRole.FREELANCER,
                null
        );

        when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    // ------------------------- LOGIN USER -------------------------
    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("test@example.com", "password123");
        LoginResponseDTO loginResponse = new LoginResponseDTO("token123", null, 86400L, "Bearer", userResponseDTO);

        when(userService.loginUser(any(UserLoginDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token123"));
    }

    // ------------------------- GET CURRENT USER PROFILE -------------------------
    @Test
    void shouldReturnCurrentUserProfile() throws Exception {
        when(userService.getCurrentUserProfile()).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    // ------------------------- UPDATE CURRENT USER PROFILE -------------------------
    @Test
    void shouldUpdateCurrentUserProfile() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", "9999999999", null);
        when(userService.updateCurrentUserProfile(any(UserUpdateDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    // ------------------------- GET USER BY ID -------------------------
    @Test
    void shouldReturnUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ------------------------- GET ALL USERS -------------------------
    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponseDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ------------------------- DELETE USER -------------------------
    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
