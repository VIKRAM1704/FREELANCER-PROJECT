package com.freelancenexus.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProjectCreateDTO;
import com.freelancenexus.projectservice.dto.ProjectDTO;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
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

    private ObjectMapper objectMapper;

    private ProjectCreateDTO createDTO;
    private ProjectDTO projectDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        objectMapper = new ObjectMapper();

        createDTO = new ProjectCreateDTO(1L, "Test Project", "Description",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(5000), 30,
                Arrays.asList("Java", "Spring"), "IT", LocalDate.now().plusDays(5));

        projectDTO = new ProjectDTO(1L, 1L, "Test Project", "Description",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(5000), 30,
                Arrays.asList("Java", "Spring"), "IT", null, LocalDate.now().plusDays(5),
                null, LocalDateTime.now(), LocalDateTime.now(), 0);
    }

    @Test
    void shouldCreateProjectSuccessfully() throws Exception {
        when(projectService.createProject(any(ProjectCreateDTO.class))).thenReturn(projectDTO);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Project"));
    }

    @Test
    void shouldGetProjectById() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(projectDTO);

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Project"));
    }

    @Test
    void shouldUpdateProjectSuccessfully() throws Exception {
        when(projectService.updateProject(eq(1L), any(ProjectCreateDTO.class))).thenReturn(projectDTO);

        mockMvc.perform(put("/api/projects/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Project"));
    }

    @Test
    void shouldDeleteProjectSuccessfully() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllProjects() throws Exception {
        List<ProjectDTO> projects = List.of(projectDTO);
        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetClientProjects() throws Exception {
        List<ProjectDTO> projects = List.of(projectDTO);
        when(projectService.getProjectsByClientId(1L)).thenReturn(projects);

        mockMvc.perform(get("/api/projects/client/{clientId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldAssignFreelancerToProject() throws Exception {
        when(projectService.assignFreelancer(1L, 2L)).thenReturn(projectDTO);

        mockMvc.perform(put("/api/projects/{projectId}/assign/{freelancerId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldHandleRuntimeException() throws Exception {
        when(projectService.getProjectById(anyLong())).thenThrow(new RuntimeException("Error occurred"));

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error occurred"));
    }

    @Test
    void shouldSearchProjectsWhenKeywordProvided() throws Exception {
        List<ProjectDTO> projects = List.of(projectDTO);
        when(projectService.searchProjects("Test", "OPEN")).thenReturn(projects);

        mockMvc.perform(get("/api/projects")
                        .param("keyword", "Test")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetProjectsByCategory() throws Exception {
        List<ProjectDTO> projects = List.of(projectDTO);
        when(projectService.getProjectsByCategory("IT")).thenReturn(projects);

        mockMvc.perform(get("/api/projects")
                        .param("category", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetOpenProjectsWhenStatusIsOpen() throws Exception {
        List<ProjectDTO> projects = List.of(projectDTO);
        when(projectService.getOpenProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
