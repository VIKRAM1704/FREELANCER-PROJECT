package com.freelancenexus.projectservice.repository;

import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClientId(Long clientId);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByCategory(String category);

    @Query("SELECT p FROM Project p WHERE p.status = :status AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Project> searchByKeywordAndStatus(@Param("keyword") String keyword, 
                                           @Param("status") ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE p.status = 'OPEN' ORDER BY p.createdAt DESC")
    List<Project> findAllOpenProjects();

    @Query("SELECT p FROM Project p WHERE p.assignedFreelancer = :freelancerId")
    List<Project> findByAssignedFreelancer(@Param("freelancerId") Long freelancerId);
}