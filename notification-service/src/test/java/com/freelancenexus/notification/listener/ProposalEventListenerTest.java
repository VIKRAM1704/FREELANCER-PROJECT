package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.ProposalEventDTO;
import com.freelancenexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProposalEventListener listener;

    private ProposalEventDTO proposalEvent;

    @BeforeEach
    void setUp() {
        proposalEvent = new ProposalEventDTO();
        proposalEvent.setProposalId(10L);
        proposalEvent.setProjectId(1L);
        proposalEvent.setProjectTitle("Test Project");
        proposalEvent.setFreelancerId(200L);
        proposalEvent.setFreelancerName("Freelancer Name");
        proposalEvent.setFreelancerEmail("freelancer@example.com");
        proposalEvent.setClientId(100L);
        proposalEvent.setClientEmail("client@example.com");
        proposalEvent.setBidAmount(250.0);
        proposalEvent.setStatus("SUBMITTED");
    }

    @Test
    void shouldHandleProposalSubmittedEvent() {
        doNothing().when(notificationService).handleProposalSubmitted(proposalEvent);

        listener.handleProposalSubmitted(proposalEvent);

        verify(notificationService, times(1)).handleProposalSubmitted(proposalEvent);
    }

    @Test
    void shouldThrowWhenProposalSubmittedFails() {
        doThrow(new RuntimeException("Service failure"))
                .when(notificationService).handleProposalSubmitted(proposalEvent);

        RuntimeException exception = null;
        try {
            listener.handleProposalSubmitted(proposalEvent);
        } catch (RuntimeException e) {
            exception = e;
        }

        assert exception != null;
        assert exception.getMessage().equals("Service failure");
        verify(notificationService, times(1)).handleProposalSubmitted(proposalEvent);
    }
}
