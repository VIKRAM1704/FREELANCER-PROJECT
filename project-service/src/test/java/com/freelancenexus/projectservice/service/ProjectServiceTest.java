package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProjectCreateDTO;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProposalRepository proposalRepository;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private ProjectService projectService;

    private Project project;
    private ProjectCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        createDTO = new ProjectCreateDTO();
        createDTO.setClientId(1L);
        createDTO.setTitle("Title");
        createDTO.setDescription("Desc");
        createDTO.setBudgetMin(BigDecimal.valueOf(100));
        createDTO.setBudgetMax(BigDecimal.valueOf(500));
        createDTO.setDurationDays(10);
        createDTO.setRequiredSkills(List.of("Java"));
        createDTO.setCategory("IT");
        createDTO.setDeadline(LocalDate.now().plusDays(5));

        project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.OPEN);
    }

    @Test
    void shouldCreateProjectSuccessfully() {
        when(projectRepository.save(any())).thenReturn(project);

        var result = projectService.createProject(createDTO);

        assertThat(result.getId()).isEqualTo(1L);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any());
    }

    @Test
    void shouldGetProjectById() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        var result = projectService.getProjectById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrow_whenProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            projectService.getProjectById(1L);
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).contains("Project not found");
        }
    }

    @Test
    void shouldAssignFreelancer() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);

        var result = projectService.assignFreelancer(1L, 2L);

        assertThat(result.getAssignedFreelancer()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }
}
