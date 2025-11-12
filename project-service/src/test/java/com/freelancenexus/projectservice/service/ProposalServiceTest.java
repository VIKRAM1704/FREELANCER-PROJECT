package com.freelancenexus.projectservice.service;

import com.freelancenexus.projectservice.dto.ProposalDTO;
import com.freelancenexus.projectservice.dto.ProposalSubmitDTO;
import com.freelancenexus.projectservice.model.Project;
import com.freelancenexus.projectservice.model.ProjectStatus;
import com.freelancenexus.projectservice.model.Proposal;
import com.freelancenexus.projectservice.model.ProposalStatus;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceTest {

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProposalService proposalService;

    private Project project;
    private Proposal proposal;
    private ProposalSubmitDTO submitDTO;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setTitle("Project 1");
        project.setStatus(ProjectStatus.OPEN);

        proposal = new Proposal();
        proposal.setId(1L);
        proposal.setProject(project);
        proposal.setFreelancerId(100L);
        proposal.setCoverLetter("Cover Letter");
        proposal.setProposedBudget(BigDecimal.valueOf(100));
        proposal.setDeliveryDays(5);
        proposal.setStatus(ProposalStatus.PENDING);

        submitDTO = new ProposalSubmitDTO();
        submitDTO.setFreelancerId(100L);
        submitDTO.setCoverLetter("Cover Letter");
        submitDTO.setProposedBudget(BigDecimal.valueOf(100));
        submitDTO.setDeliveryDays(5);
    }

    @Test
    void shouldSubmitProposalSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(proposalRepository.existsByProjectIdAndFreelancerId(1L, 100L)).thenReturn(false);
        when(proposalRepository.save(any(Proposal.class))).thenReturn(proposal);

        ProposalDTO dto = proposalService.submitProposal(1L, submitDTO);

        assertNotNull(dto);
        assertEquals(100L, dto.getFreelancerId());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(ProposalDTO.class));
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> proposalService.submitProposal(1L, submitDTO));
        assertTrue(ex.getMessage().contains("Project not found"));
    }

    @Test
    void shouldThrowWhenProjectNotOpen() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> proposalService.submitProposal(1L, submitDTO));
        assertTrue(ex.getMessage().contains("Project is not accepting proposals"));
    }

    @Test
    void shouldThrowWhenFreelancerAlreadySubmitted() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(proposalRepository.existsByProjectIdAndFreelancerId(1L, 100L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> proposalService.submitProposal(1L, submitDTO));
        assertTrue(ex.getMessage().contains("Freelancer already submitted"));
    }

    @Test
    void shouldFetchProposalsByProjectId() {
        when(proposalRepository.findByProjectId(1L)).thenReturn(List.of(proposal));

        List<ProposalDTO> list = proposalService.getProposalsByProjectId(1L);

        assertEquals(1, list.size());
    }

    @Test
    void shouldFetchProposalsByFreelancerId() {
        when(proposalRepository.findByFreelancerId(100L)).thenReturn(List.of(proposal));

        List<ProposalDTO> list = proposalService.getProposalsByFreelancerId(100L);

        assertEquals(1, list.size());
    }

    @Test
    void shouldFetchProposalById() {
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));

        ProposalDTO dto = proposalService.getProposalById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void shouldAcceptProposal() {
        Proposal other = new Proposal();
        other.setId(2L);
        other.setProject(project);
        other.setStatus(ProposalStatus.PENDING);

        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(proposalRepository.findByProjectIdAndStatus(1L, ProposalStatus.PENDING)).thenReturn(List.of(proposal, other));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProposalDTO dto = proposalService.acceptProposal(1L);

        assertEquals(ProposalStatus.ACCEPTED, proposal.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
        assertEquals(proposal.getFreelancerId(), project.getAssignedFreelancer());
    }

    @Test
    void shouldRejectProposal() {
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(any(Proposal.class))).thenReturn(proposal);

        ProposalDTO dto = proposalService.rejectProposal(1L);

        assertEquals(ProposalStatus.REJECTED, proposal.getStatus());
    }

    @Test
    void shouldGetRankedProposals() {
        when(proposalRepository.findByProjectIdOrderByAiScoreDesc(1L)).thenReturn(List.of(proposal));

        List<ProposalDTO> ranked = proposalService.getRankedProposalsByProjectId(1L);

        assertEquals(1, ranked.size());
    }
}
