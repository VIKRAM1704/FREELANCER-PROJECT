package com.freelancenexus.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProjectCreateDTO;
import com.freelancenexus.projectservice.dto.ProjectDTO;
import com.freelancenexus.projectservice.model.ProjectStatus;
import com.freelancenexus.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProjectDTO sampleProject;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(projectController)
                .build();

        sampleProject = new ProjectDTO(1L, 1L, "Title", "Desc", 
                BigDecimal.valueOf(100), BigDecimal.valueOf(500), 10, List.of("Java"),
                "IT", ProjectStatus.OPEN, LocalDate.now().plusDays(10), null, null, null, 0);
    }

    @Test
    void shouldCreateProject_whenPostCalled() throws Exception {
        ProjectCreateDTO createDTO = new ProjectCreateDTO(1L, "Title", "Desc", 
                BigDecimal.valueOf(100), BigDecimal.valueOf(500), 10, List.of("Java"), "IT", LocalDate.now().plusDays(5));
        when(projectService.createProject(any(ProjectCreateDTO.class))).thenReturn(sampleProject);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProject)));
    }

    @Test
    void shouldGetProject_whenGetByIdCalled() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(sampleProject);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProject)));
    }

    @Test
    void shouldUpdateProject_whenPutCalled() throws Exception {
        ProjectCreateDTO updateDTO = new ProjectCreateDTO(1L, "Updated", "Desc", 
                BigDecimal.valueOf(200), BigDecimal.valueOf(600), 12, List.of("Java"), "IT", LocalDate.now().plusDays(5));
        when(projectService.updateProject(any(Long.class), any(ProjectCreateDTO.class))).thenReturn(sampleProject);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleProject)));
    }

    @Test
    void shouldDeleteProject_whenDeleteCalled() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequest_whenServiceThrowsException() throws Exception {
        when(projectService.getProjectById(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not found"));
    }
}
