package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.ProjectEventDTO;
import com.freelancenexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProjectEventListener listener;

    private ProjectEventDTO projectEvent;

    @BeforeEach
    void setUp() {
        projectEvent = new ProjectEventDTO();
        projectEvent.setProjectId(1L);
        projectEvent.setClientEmail("client@example.com");
        projectEvent.setProjectTitle("Test Project");
        projectEvent.setAssignedFreelancerId(200L);
        projectEvent.setFreelancerEmail("freelancer@example.com");
    }

    @Test
    void shouldHandleProjectCreatedEvent() {
        // Simulate service call success
        doNothing().when(notificationService).handleProjectCreated(projectEvent);

        listener.handleProjectCreated(projectEvent);

        verify(notificationService, times(1)).handleProjectCreated(projectEvent);
    }

    @Test
    void shouldThrowWhenProjectCreatedFails() {
        doThrow(new RuntimeException("Service failure"))
                .when(notificationService).handleProjectCreated(projectEvent);

        RuntimeException exception = null;
        try {
            listener.handleProjectCreated(projectEvent);
        } catch (RuntimeException e) {
            exception = e;
        }

        assert exception != null;
        assert exception.getMessage().equals("Service failure");
        verify(notificationService, times(1)).handleProjectCreated(projectEvent);
    }

    @Test
    void shouldHandleProjectAssignedEvent() {
        doNothing().when(notificationService).handleProjectAssigned(projectEvent);

        listener.handleProjectAssigned(projectEvent);

        verify(notificationService, times(1)).handleProjectAssigned(projectEvent);
    }

    @Test
    void shouldThrowWhenProjectAssignedFails() {
        doThrow(new RuntimeException("Service failure"))
                .when(notificationService).handleProjectAssigned(projectEvent);

        RuntimeException exception = null;
        try {
            listener.handleProjectAssigned(projectEvent);
        } catch (RuntimeException e) {
            exception = e;
        }

        assert exception != null;
        assert exception.getMessage().equals("Service failure");
        verify(notificationService, times(1)).handleProjectAssigned(projectEvent);
    }
}
