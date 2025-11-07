package com.freelancenexus.projectservice.repository;

import com.freelancenexus.projectservice.model.Proposal;
import com.freelancenexus.projectservice.model.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByProjectId(Long projectId);

    List<Proposal> findByFreelancerId(Long freelancerId);

    List<Proposal> findByProjectIdAndStatus(Long projectId, ProposalStatus status);

    Optional<Proposal> findByProjectIdAndFreelancerId(Long projectId, Long freelancerId);

    @Query("SELECT p FROM Proposal p WHERE p.project.id = :projectId ORDER BY p.aiScore DESC, p.submittedAt ASC")
    List<Proposal> findByProjectIdOrderByAiScoreDesc(@Param("projectId") Long projectId);

    boolean existsByProjectIdAndFreelancerId(Long projectId, Long freelancerId);

    long countByProjectId(Long projectId);
}