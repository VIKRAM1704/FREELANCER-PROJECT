package com.freelancenexus.projectservice.controller;

import com.freelancenexus.projectservice.dto.ProposalDTO;
import com.freelancenexus.projectservice.dto.ProposalSubmitDTO;
import com.freelancenexus.projectservice.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ProposalController {

    private final ProposalService proposalService;

    // Only freelancers can submit proposals
    @PostMapping("/projects/{projectId}/proposals")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<ProposalDTO> submitProposal(
            @PathVariable Long projectId,
            @Valid @RequestBody ProposalSubmitDTO submitDTO) {
        log.info("POST /api/projects/{}/proposals - Submitting proposal", projectId);
        ProposalDTO proposal = proposalService.submitProposal(projectId, submitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(proposal);
    }

    // Client can view proposals for their projects
    @GetMapping("/projects/{projectId}/proposals")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ProposalDTO>> getProjectProposals(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "false") boolean ranked) {
        log.info("GET /api/projects/{}/proposals - Fetching proposals", projectId);
        
        List<ProposalDTO> proposals;
        if (ranked) {
            proposals = proposalService.getRankedProposalsByProjectId(projectId);
        } else {
            proposals = proposalService.getProposalsByProjectId(projectId);
        }
        
        return ResponseEntity.ok(proposals);
    }

    // Both client and freelancer can view proposal details
    @GetMapping("/proposals/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<ProposalDTO> getProposal(@PathVariable Long id) {
        log.info("GET /api/proposals/{} - Fetching proposal", id);
        ProposalDTO proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    // Freelancer can view their own proposals
    @GetMapping("/proposals/freelancer/{freelancerId}")
    @PreAuthorize("hasRole('FREELANCER')")
    public ResponseEntity<List<ProposalDTO>> getFreelancerProposals(@PathVariable Long freelancerId) {
        log.info("GET /api/proposals/freelancer/{} - Fetching freelancer proposals", freelancerId);
        List<ProposalDTO> proposals = proposalService.getProposalsByFreelancerId(freelancerId);
        return ResponseEntity.ok(proposals);
    }

    // Only client can accept proposals
    @PutMapping("/proposals/{id}/accept")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ProposalDTO> acceptProposal(@PathVariable Long id) {
        log.info("PUT /api/proposals/{}/accept - Accepting proposal", id);
        ProposalDTO proposal = proposalService.acceptProposal(id);
        return ResponseEntity.ok(proposal);
    }

    // Only client can reject proposals
    @PutMapping("/proposals/{id}/reject")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ProposalDTO> rejectProposal(@PathVariable Long id) {
        log.info("PUT /api/proposals/{}/reject - Rejecting proposal", id);
        ProposalDTO proposal = proposalService.rejectProposal(id);
        return ResponseEntity.ok(proposal);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Error in ProposalController", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}