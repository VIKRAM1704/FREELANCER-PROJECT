package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.NotificationDTO;
import com.freelancenexus.notification.event.ProjectCreatedEvent;
import com.freelancenexus.notification.event.ProjectUpdatedEvent;
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
class ProjectEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Logger log;

    @InjectMocks
    private ProjectEventListener projectEventListener;

    private ProjectCreatedEvent createdEvent;
    private ProjectUpdatedEvent updatedEvent;

    @BeforeEach
    void setUp() {
        createdEvent = new ProjectCreatedEvent(this, "user123", "New AI Project");
        updatedEvent = new ProjectUpdatedEvent(this, "user123", "Updated AI Project");
    }

    @Test
    void testHandleProjectCreated_Success() {
        doNothing().when(notificationService).sendNotification(any(NotificationDTO.class));

        projectEventListener.handleProjectCreated(createdEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProjectUpdated_Success() {
        doNothing().when(notificationService).sendNotification(any(NotificationDTO.class));

        projectEventListener.handleProjectUpdated(updatedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandleProjectCreated_ExceptionHandled() {
        doThrow(new RuntimeException("Boom")).when(notificationService).sendNotification(any(NotificationDTO.class));

        projectEventListener.handleProjectCreated(createdEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
        // Exception is caught internally, no throw expected
    }

    @Test
    void testHandleProjectUpdated_ExceptionHandled() {
        doThrow(new RuntimeException("Crash")).when(notificationService).sendNotification(any(NotificationDTO.class));

        projectEventListener.handleProjectUpdated(updatedEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }
}
