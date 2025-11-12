package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.NotificationDTO;
import com.freelancenexus.notification.event.ProposalAcceptedEvent;
import com.freelancenexus.notification.event.ProposalRejectedEvent;
import com.freelancenexus.notification.event.ProposalSubmittedEvent;
import com.freelancenexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Logger log;

    @InjectMocks
    private ProposalEventListener proposalEventListener;

    private ProposalSubmittedEvent submittedEvent;
    private ProposalAcceptedEvent acceptedEvent;
    private ProposalRejectedEvent rejectedEvent;

    @BeforeEach
    void setUp() {
        submittedEvent = new ProposalSubmittedEvent(this, "freelancer123", "Cool Project");
        acceptedEvent = new ProposalAcceptedEvent(this, "freelancer123", "Cool Project");
        rejectedEvent = new ProposalRejectedEvent(this, "freelancer123", "Cool Project");
    }

    @Test
    void testHandleProposalSubmitted_Success() {
        doNothing().when(notificationService).sendNotification(any(NotificationDTO.class));

        proposalEventListener.handleProposalSubmitted(submittedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProposalAccepted_Success() {
        doNothing().when(notificationService).sendNotification(any(NotificationDTO.class));

        proposalEventListener.handleProposalAccepted(acceptedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProposalRejected_Success() {
        doNothing().when(notificationService).sendNotification(any(NotificationDTO.class));

        proposalEventListener.handleProposalRejected(rejectedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProposalSubmitted_ExceptionHandled() {
        doThrow(new RuntimeException("Mail fail")).when(notificationService).sendNotification(any(NotificationDTO.class));

        proposalEventListener.handleProposalSubmitted(submittedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProposalAccepted_ExceptionHandled() {
        doThrow(new RuntimeException("Mail fail")).when(notificationService).sendNotification(any(NotificationDTO.class));

        proposalEventListener.handleProposalAccepted(acceptedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProposalRejected_ExceptionHandled() {
        doThrow(new RuntimeException("Mail fail")).when(notificationService).sendNotification(any(NotificationDTO.class));

        proposalEventListener.handleProposalRejected(rejectedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }
}
