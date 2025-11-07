package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.dto.ProjectCreateDTO;
import com.freelancenexus.projectservice.dto.ProjectDTO;
import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.ProjectStatus;
import com.freelancenexus.projectservice.repository.ProjectRepository;
import com.freelancenexus.projectservice.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProposalRepository proposalRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.project}")
    private String projectExchange;

    @Value("${rabbitmq.routing.project.created}")
    private String projectCreatedRoutingKey;

    @Transactional
    public ProjectDTO createProject(ProjectCreateDTO createDTO) {
        log.info("Creating new project: {}", createDTO.getTitle());

        Project project = new Project();
        project.setClientId(createDTO.getClientId());
        project.setTitle(createDTO.getTitle());
        project.setDescription(createDTO.getDescription());
        project.setBudgetMin(createDTO.getBudgetMin());
        project.setBudgetMax(createDTO.getBudgetMax());
        project.setDurationDays(createDTO.getDurationDays());
        project.setRequiredSkills(convertSkillsToJson(createDTO.getRequiredSkills()));
        project.setCategory(createDTO.getCategory());
        project.setStatus(ProjectStatus.OPEN);
        project.setDeadline(createDTO.getDeadline());

        Project savedProject = projectRepository.save(project);
        log.info("Project created with ID: {}", savedProject.getId());

        // Publish event to RabbitMQ
        publishProjectCreatedEvent(savedProject);

        return convertToDTO(savedProject);
    }

    public ProjectDTO getProjectById(Long id) {
        log.info("Fetching project by ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        return convertToDTO(project);
    }

    @Transactional
    public ProjectDTO updateProject(Long id, ProjectCreateDTO updateDTO) {
        log.info("Updating project ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        project.setTitle(updateDTO.getTitle());
        project.setDescription(updateDTO.getDescription());
        project.setBudgetMin(updateDTO.getBudgetMin());
        project.setBudgetMax(updateDTO.getBudgetMax());
        project.setDurationDays(updateDTO.getDurationDays());
        project.setRequiredSkills(convertSkillsToJson(updateDTO.getRequiredSkills()));
        project.setCategory(updateDTO.getCategory());
        project.setDeadline(updateDTO.getDeadline());

        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        log.info("Deleting project ID: {}", id);
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        
        project.setStatus(ProjectStatus.CANCELLED);
        projectRepository.save(project);
    }

    public List<ProjectDTO> getAllProjects() {
        log.info("Fetching all projects");
        return projectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByClientId(Long clientId) {
        log.info("Fetching projects for client ID: {}", clientId);
        return projectRepository.findByClientId(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getOpenProjects() {
        log.info("Fetching all open projects");
        return projectRepository.findAllOpenProjects().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> searchProjects(String keyword, String status) {
        log.info("Searching projects with keyword: {} and status: {}", keyword, status);
        
        ProjectStatus projectStatus = status != null ? ProjectStatus.valueOf(status.toUpperCase()) : ProjectStatus.OPEN;
        
        return projectRepository.searchByKeywordAndStatus(keyword, projectStatus).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByCategory(String category) {
        log.info("Fetching projects by category: {}", category);
        return projectRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO assignFreelancer(Long projectId, Long freelancerId) {
        log.info("Assigning freelancer {} to project {}", freelancerId, projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        project.setAssignedFreelancer(freelancerId);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setClientId(project.getClientId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setBudgetMin(project.getBudgetMin());
        dto.setBudgetMax(project.getBudgetMax());
        dto.setDurationDays(project.getDurationDays());
        dto.setRequiredSkills(parseSkillsFromJson(project.getRequiredSkills()));
        dto.setCategory(project.getCategory());
        dto.setStatus(project.getStatus());
        dto.setDeadline(project.getDeadline());
        dto.setAssignedFreelancer(project.getAssignedFreelancer());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        dto.setProposalCount((int) proposalRepository.countByProjectId(project.getId()));
        return dto;
    }

    private String convertSkillsToJson(List<String> skills) {
        try {
            return objectMapper.writeValueAsString(skills);
        } catch (Exception e) {
            log.error("Error converting skills to JSON", e);
            return "[]";
        }
    }

    private List<String> parseSkillsFromJson(String skillsJson) {
        try {
            return objectMapper.readValue(skillsJson, List.class);
        } catch (Exception e) {
            log.error("Error parsing skills from JSON", e);
            return Arrays.asList(skillsJson.split(","));
        }
    }

    private void publishProjectCreatedEvent(Project project) {
        try {
            ProjectDTO dto = convertToDTO(project);
            rabbitTemplate.convertAndSend(projectExchange, projectCreatedRoutingKey, dto);
            log.info("Published project.created event for project ID: {}", project.getId());
        } catch (Exception e) {
            log.error("Error publishing project.created event", e);
        }
    }
}