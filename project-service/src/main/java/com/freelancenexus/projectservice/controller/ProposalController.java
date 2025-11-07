package com.freelancenexus.projectservice.controller;

import com.freelancenexus.projectservice.dto.ProposalDTO;
import com.freelancenexus.projectservice.dto.ProposalSubmitDTO;
import com.freelancenexus.projectservice.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping("/projects/{projectId}/proposals")
    public ResponseEntity<ProposalDTO> submitProposal(
            @PathVariable Long projectId,
            @Valid @RequestBody ProposalSubmitDTO submitDTO) {
        log.info("POST /api/projects/{}/proposals - Submitting proposal", projectId);
        ProposalDTO proposal = proposalService.submitProposal(projectId, submitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(proposal);
    }

    @GetMapping("/projects/{projectId}/proposals")
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

    @GetMapping("/proposals/{id}")
    public ResponseEntity<ProposalDTO> getProposal(@PathVariable Long id) {
        log.info("GET /api/proposals/{} - Fetching proposal", id);
        ProposalDTO proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    @GetMapping("/proposals/freelancer/{freelancerId}")
    public ResponseEntity<List<ProposalDTO>> getFreelancerProposals(@PathVariable Long freelancerId) {
        log.info("GET /api/proposals/freelancer/{} - Fetching freelancer proposals", freelancerId);
        List<ProposalDTO> proposals = proposalService.getProposalsByFreelancerId(freelancerId);
        return ResponseEntity.ok(proposals);
    }

    @PutMapping("/proposals/{id}/accept")
    public ResponseEntity<ProposalDTO> acceptProposal(@PathVariable Long id) {
        log.info("PUT /api/proposals/{}/accept - Accepting proposal", id);
        ProposalDTO proposal = proposalService.acceptProposal(id);
        return ResponseEntity.ok(proposal);
    }

    @PutMapping("/proposals/{id}/reject")
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