package com.freelancenexus.projectservice.service;

import com.freelancenexus.projectservice.dto.ProposalDTO;
import com.freelancenexus.projectservice.dto.ProposalSubmitDTO;
import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.Proposal;
import com.freelancenexus.projectservice.model.ProposalStatus;
import com.freelancenexus.projectservice.repository.ProjectRepository;
import com.freelancenexus.projectservice.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProjectRepository projectRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.project}")
    private String projectExchange;

    @Value("${rabbitmq.routing.proposal.submitted}")
    private String proposalSubmittedRoutingKey;

    @Transactional
    public ProposalDTO submitProposal(Long projectId, ProposalSubmitDTO submitDTO) {
        log.info("Submitting proposal for project ID: {} by freelancer: {}", projectId, submitDTO.getFreelancerId());

        // Verify project exists and is open
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        if (project.getStatus() != com.freelancenexus.projectservice.model.ProjectStatus.OPEN) {
            throw new RuntimeException("Project is not accepting proposals");
        }

        // Check if freelancer already submitted a proposal
        if (proposalRepository.existsByProjectIdAndFreelancerId(projectId, submitDTO.getFreelancerId())) {
            throw new RuntimeException("Freelancer already submitted a proposal for this project");
        }

        Proposal proposal = new Proposal();
        proposal.setProject(project);
        proposal.setFreelancerId(submitDTO.getFreelancerId());
        proposal.setCoverLetter(submitDTO.getCoverLetter());
        proposal.setProposedBudget(submitDTO.getProposedBudget());
        proposal.setDeliveryDays(submitDTO.getDeliveryDays());
        proposal.setStatus(ProposalStatus.PENDING);

        Proposal savedProposal = proposalRepository.save(proposal);
        log.info("Proposal created with ID: {}", savedProposal.getId());

        // Publish event to RabbitMQ
        publishProposalSubmittedEvent(savedProposal);

        return convertToDTO(savedProposal);
    }

    public List<ProposalDTO> getProposalsByProjectId(Long projectId) {
        log.info("Fetching proposals for project ID: {}", projectId);
        return proposalRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProposalDTO> getProposalsByFreelancerId(Long freelancerId) {
        log.info("Fetching proposals for freelancer ID: {}", freelancerId);
        return proposalRepository.findByFreelancerId(freelancerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProposalDTO getProposalById(Long id) {
        log.info("Fetching proposal by ID: {}", id);
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + id));
        return convertToDTO(proposal);
    }

    @Transactional
    public ProposalDTO acceptProposal(Long proposalId) {
        log.info("Accepting proposal ID: {}", proposalId);

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new RuntimeException("Proposal is not in pending status");
        }

        proposal.setStatus(ProposalStatus.ACCEPTED);
        
        // Reject all other proposals for the same project
        List<Proposal> otherProposals = proposalRepository.findByProjectIdAndStatus(
                proposal.getProject().getId(), ProposalStatus.PENDING);
        
        otherProposals.stream()
                .filter(p -> !p.getId().equals(proposalId))
                .forEach(p -> {
                    p.setStatus(ProposalStatus.REJECTED);
                    proposalRepository.save(p);
                });

        Proposal acceptedProposal = proposalRepository.save(proposal);
        
        // Update project status
        Project project = proposal.getProject();
        project.setAssignedFreelancer(proposal.getFreelancerId());
        project.setStatus(com.freelancenexus.projectservice.model.ProjectStatus.IN_PROGRESS);
        projectRepository.save(project);

        return convertToDTO(acceptedProposal);
    }

    @Transactional
    public ProposalDTO rejectProposal(Long proposalId) {
        log.info("Rejecting proposal ID: {}", proposalId);

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new RuntimeException("Proposal is not in pending status");
        }

        proposal.setStatus(ProposalStatus.REJECTED);
        Proposal rejectedProposal = proposalRepository.save(proposal);

        return convertToDTO(rejectedProposal);
    }

    public List<ProposalDTO> getRankedProposalsByProjectId(Long projectId) {
        log.info("Fetching ranked proposals for project ID: {}", projectId);
        return proposalRepository.findByProjectIdOrderByAiScoreDesc(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProposalDTO convertToDTO(Proposal proposal) {
        ProposalDTO dto = new ProposalDTO();
        dto.setId(proposal.getId());
        dto.setProjectId(proposal.getProject().getId());
        dto.setProjectTitle(proposal.getProject().getTitle());
        dto.setFreelancerId(proposal.getFreelancerId());
        dto.setCoverLetter(proposal.getCoverLetter());
        dto.setProposedBudget(proposal.getProposedBudget());
        dto.setDeliveryDays(proposal.getDeliveryDays());
        dto.setAiScore(proposal.getAiScore());
        dto.setStatus(proposal.getStatus());
        dto.setSubmittedAt(proposal.getSubmittedAt());
        dto.setUpdatedAt(proposal.getUpdatedAt());
        return dto;
    }

    private void publishProposalSubmittedEvent(Proposal proposal) {
        try {
            ProposalDTO dto = convertToDTO(proposal);
            rabbitTemplate.convertAndSend(projectExchange, proposalSubmittedRoutingKey, dto);
            log.info("Published proposal.submitted event for proposal ID: {}", proposal.getId());
        } catch (Exception e) {
            log.error("Error publishing proposal.submitted event", e);
        }
    }
}