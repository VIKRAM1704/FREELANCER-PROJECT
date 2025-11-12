package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProjectCreateDTO;
import com.freelancenexus.projectservice.dto.ProjectDTO;
import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.ProjectStatus;
import com.freelancenexus.projectservice.repository.ProjectRepository;
import com.freelancenexus.projectservice.repository.ProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectCreateDTO createDTO;

    @BeforeEach
    void setUp() throws Exception {
        createDTO = new ProjectCreateDTO(
                1L, "Title", "Description",
                BigDecimal.valueOf(100), BigDecimal.valueOf(200),
                10, List.of("Java", "Spring"), "Software", LocalDate.now().plusDays(5)
        );

        project = new Project();
        project.setId(1L);
        project.setClientId(createDTO.getClientId());
        project.setTitle(createDTO.getTitle());
        project.setDescription(createDTO.getDescription());
        project.setBudgetMin(createDTO.getBudgetMin());
        project.setBudgetMax(createDTO.getBudgetMax());
        project.setDurationDays(createDTO.getDurationDays());
        project.setRequiredSkills("[]");
        project.setCategory(createDTO.getCategory());
        project.setStatus(ProjectStatus.OPEN);
        project.setDeadline(createDTO.getDeadline());

        when(objectMapper.writeValueAsString(anyList())).thenReturn("[]");
        when(objectMapper.readValue(anyString(), eq(List.class))).thenReturn(createDTO.getRequiredSkills());
    }

    @Test
    void shouldCreateProjectSuccessfully() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        ProjectDTO dto = projectService.createProject(createDTO);

        assertNotNull(dto);
        assertEquals("Title", dto.getTitle());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(ProjectDTO.class));
    }

    @Test
    void shouldFetchProjectById() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        ProjectDTO dto = projectService.getProjectById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void shouldThrowExceptionWhenProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> projectService.getProjectById(99L));
        assertTrue(ex.getMessage().contains("Project not found"));
    }

    @Test
    void shouldUpdateProjectSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        ProjectDTO dto = projectService.updateProject(1L, createDTO);

        assertNotNull(dto);
        assertEquals("Title", dto.getTitle());
    }

    @Test
    void shouldDeleteProjectSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        assertDoesNotThrow(() -> projectService.deleteProject(1L));
        assertEquals(ProjectStatus.CANCELLED, project.getStatus());
    }

    @Test
    void shouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        List<ProjectDTO> projects = projectService.getAllProjects();

        assertEquals(1, projects.size());
    }

    @Test
    void shouldReturnProjectsByClientId() {
        when(projectRepository.findByClientId(1L)).thenReturn(List.of(project));
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        List<ProjectDTO> projects = projectService.getProjectsByClientId(1L);

        assertEquals(1, projects.size());
    }

    @Test
    void shouldReturnOpenProjects() {
        when(projectRepository.findAllOpenProjects()).thenReturn(List.of(project));
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        List<ProjectDTO> projects = projectService.getOpenProjects();

        assertEquals(1, projects.size());
    }

    @Test
    void shouldSearchProjectsByKeywordAndStatus() {
        when(projectRepository.searchByKeywordAndStatus(anyString(), any())).thenReturn(List.of(project));
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        List<ProjectDTO> projects = projectService.searchProjects("Title", "OPEN");

        assertEquals(1, projects.size());
    }

    @Test
    void shouldReturnProjectsByCategory() {
        when(projectRepository.findByCategory("Software")).thenReturn(List.of(project));
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        List<ProjectDTO> projects = projectService.getProjectsByCategory("Software");

        assertEquals(1, projects.size());
    }

    @Test
    void shouldAssignFreelancerToProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(proposalRepository.countByProjectId(anyLong())).thenReturn(0L);

        ProjectDTO dto = projectService.assignFreelancer(1L, 100L);

        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
        assertEquals(100L, dto.getAssignedFreelancer());
    }
}
