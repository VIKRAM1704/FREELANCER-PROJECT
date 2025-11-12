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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceTest {

    @Mock private ProposalRepository proposalRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private ProposalService proposalService;

    private Project project;
    private Proposal proposal;
    private ProposalSubmitDTO submitDTO;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.OPEN);

        proposal = new Proposal();
        proposal.setId(1L);
        proposal.setProject(project);
        proposal.setStatus(ProposalStatus.PENDING);

        submitDTO = new ProposalSubmitDTO();
        submitDTO.setFreelancerId(1L);
        submitDTO.setCoverLetter("This is a valid cover letter with more than 50 characters...");
        submitDTO.setProposedBudget(BigDecimal.valueOf(200));
        submitDTO.setDeliveryDays(5);
    }

    @Test
    void shouldSubmitProposalSuccessfully() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(proposalRepository.existsByProjectIdAndFreelancerId(1L, 1L)).thenReturn(false);
        when(proposalRepository.save(any())).thenReturn(proposal);

        ProposalDTO dto = proposalService.submitProposal(1L, submitDTO);

        assertThat(dto.getId()).isEqualTo(1L);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any());
    }

    @Test
    void shouldThrowIfProjectNotOpen() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        try {
            proposalService.submitProposal(1L, submitDTO);
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).contains("Project is not accepting proposals");
        }
    }

    @Test
    void shouldAcceptProposal() {
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(proposal));
        when(proposalRepository.findByProjectIdAndStatus(1L, ProposalStatus.PENDING)).thenReturn(List.of(proposal));
        when(proposalRepository.save(any())).thenReturn(proposal);
        when(projectRepository.save(any())).thenReturn(project);

        ProposalDTO dto = proposalService.acceptProposal(1L);

        assertThat(dto.getStatus()).isEqualTo(ProposalStatus.ACCEPTED);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }
}
