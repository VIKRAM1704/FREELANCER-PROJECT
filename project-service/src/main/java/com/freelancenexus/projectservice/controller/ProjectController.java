package com.freelancenexus.projectservice.controller;

import com.freelancenexus.projectservice.dto.ProjectCreateDTO;
import com.freelancenexus.projectservice.dto.ProjectDTO;
import com.freelancenexus.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) {
        log.info("POST /api/projects - Creating project: {}", createDTO.getTitle());
        ProjectDTO createdProject = projectService.createProject(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        log.info("GET /api/projects/{} - Fetching project", id);
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateDTO updateDTO) {
        log.info("PUT /api/projects/{} - Updating project", id);
        ProjectDTO updatedProject = projectService.updateProject(id, updateDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("DELETE /api/projects/{} - Deleting project", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        log.info("GET /api/projects - Fetching projects with filters");
        
        List<ProjectDTO> projects;
        
        if (keyword != null && !keyword.isEmpty()) {
            projects = projectService.searchProjects(keyword, status);
        } else if (category != null && !category.isEmpty()) {
            projects = projectService.getProjectsByCategory(category);
        } else if ("OPEN".equalsIgnoreCase(status)) {
            projects = projectService.getOpenProjects();
        } else {
            projects = projectService.getAllProjects();
        }
        
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProjectDTO>> getClientProjects(@PathVariable Long clientId) {
        log.info("GET /api/projects/client/{} - Fetching client projects", clientId);
        List<ProjectDTO> projects = projectService.getProjectsByClientId(clientId);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}/assign/{freelancerId}")
    public ResponseEntity<ProjectDTO> assignFreelancer(
            @PathVariable Long projectId,
            @PathVariable Long freelancerId) {
        log.info("PUT /api/projects/{}/assign/{} - Assigning freelancer", projectId, freelancerId);
        ProjectDTO project = projectService.assignFreelancer(projectId, freelancerId);
        return ResponseEntity.ok(project);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Error in ProjectController", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}