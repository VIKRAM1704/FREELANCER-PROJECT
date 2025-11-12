package com.freelancenexus.notification.controller;

import com.freelancenexus.notification.model.Notification;
import com.freelancenexus.notification.model.NotificationType;
import com.freelancenexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NotificationController using MockMvc.
 *
 * Tests cover:
 *  - getUserNotifications (positive + service throws)
 *  - getUnreadNotifications (positive + empty list)
 *  - getUnreadCount (positive)
 *  - markAsRead (positive + not found -> 500)
 *  - markAllAsRead (positive + service throws -> 500)
 *  - healthCheck
 *
 * Notes:
 *  - Notification model is created with setters consistent with the project code.
 *  - Controller uses NotificationService which is mocked with @MockBean.
 */
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private Notification sampleNotification1;
    private Notification sampleNotification2;

    @BeforeEach
    void setUp() {
        // Create two sample Notification objects - controller converts them to DTOs
        sampleNotification1 = new Notification();
        sampleNotification1.setId(1L);
        sampleNotification1.setUserId(100L);
        sampleNotification1.setType(NotificationType.PROJECT_CREATED);
        sampleNotification1.setTitle("Project Posted Successfully");
        sampleNotification1.setMessage("Your project 'X' has been posted");
        sampleNotification1.setIsRead(false);
        sampleNotification1.setEmailSent(Boolean.TRUE);
        sampleNotification1.setRecipientEmail("client@example.com");
        sampleNotification1.setCreatedAt(LocalDateTime.now().minusHours(1));
        sampleNotification1.setReadAt(null);

        sampleNotification2 = new Notification();
        sampleNotification2.setId(2L);
        sampleNotification2.setUserId(100L);
        sampleNotification2.setType(NotificationType.PROPOSAL_SUBMITTED);
        sampleNotification2.setTitle("New Proposal Received");
        sampleNotification2.setMessage("Freelancer Y submitted a proposal");
        sampleNotification2.setIsRead(false);
        sampleNotification2.setEmailSent(Boolean.FALSE);
        sampleNotification2.setRecipientEmail("client@example.com");
        sampleNotification2.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        sampleNotification2.setReadAt(null);
    }

    @Test
    @DisplayName("shouldReturnNotifications_whenGetUserNotificationsIsCalled")
    void shouldReturnNotifications_whenGetUserNotificationsIsCalled() throws Exception {
        // Arrange
        List<Notification> notifications = Arrays.asList(sampleNotification1, sampleNotification2);
        Mockito.when(notificationService.getUserNotifications(100L)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}", 100L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // verify array size and some properties
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(100))
                .andExpect(jsonPath("$[0].type").value("PROJECT_CREATED"))
                .andExpect(jsonPath("$[0].title").value("Project Posted Successfully"))
                // createdAt should be present and not null for the DTO
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenServiceThrowsInGetUserNotifications")
    void shouldReturnInternalServerError_whenServiceThrowsInGetUserNotifications() throws Exception {
        // Arrange - simulate service failure
        Mockito.when(notificationService.getUserNotifications(anyLong()))
                .thenThrow(new RuntimeException("database down"));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("shouldReturnUnreadNotifications_whenGetUnreadNotificationsIsCalled")
    void shouldReturnUnreadNotifications_whenGetUnreadNotificationsIsCalled() throws Exception {
        // Arrange - return a single unread notification
        Mockito.when(notificationService.getUnreadNotifications(100L))
                .thenReturn(Collections.singletonList(sampleNotification2));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}/unread", 100L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("PROPOSAL_SUBMITTED"))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    @DisplayName("shouldReturnEmptyList_whenNoUnreadNotifications")
    void shouldReturnEmptyList_whenNoUnreadNotifications() throws Exception {
        // Arrange - empty list
        Mockito.when(notificationService.getUnreadNotifications(200L))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}/unread", 200L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("shouldReturnUnreadCount_whenGetUnreadCountIsCalled")
    void shouldReturnUnreadCount_whenGetUnreadCountIsCalled() throws Exception {
        // Arrange - two unread notifications
        Mockito.when(notificationService.getUnreadNotifications(100L))
                .thenReturn(Arrays.asList(sampleNotification1, sampleNotification2));

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}/unread/count", 100L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // controller returns Long count as JSON number
                .andExpect(content().string("2"));
    }

    @Test
    @DisplayName("shouldMarkAsReadAndReturnNotificationDTO_whenMarkAsReadIsCalled")
    void shouldMarkAsReadAndReturnNotificationDTO_whenMarkAsReadIsCalled() throws Exception {
        // Arrange - simulate marking as read
        Notification marked = new Notification();
        marked.setId(1L);
        marked.setUserId(100L);
        marked.setType(NotificationType.PROJECT_CREATED);
        marked.setTitle("Project Posted Successfully");
        marked.setMessage("Your project 'X' has been posted");
        marked.setIsRead(true);
        marked.setEmailSent(true);
        marked.setRecipientEmail("client@example.com");
        marked.setCreatedAt(LocalDateTime.now().minusHours(1));
        marked.setReadAt(LocalDateTime.now());

        Mockito.when(notificationService.markAsRead(1L)).thenReturn(marked);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/{id}/read", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isRead").value(true))
                .andExpect(jsonPath("$.readAt").exists());
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenMarkAsReadThrows")
    void shouldReturnInternalServerError_whenMarkAsReadThrows() throws Exception {
        // Arrange - service throws when not found
        Mockito.when(notificationService.markAsRead(42L))
                .thenThrow(new RuntimeException("Notification not found"));

        // Act & Assert
        mockMvc.perform(put("/api/notifications/{id}/read", 42L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("shouldMarkAllAsReadAndReturnMessage_whenMarkAllAsReadIsCalled")
    void shouldMarkAllAsReadAndReturnMessage_whenMarkAllAsReadIsCalled() throws Exception {
        // Arrange - service returns void successfully
        doNothing().when(notificationService).markAllAsRead(100L);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/user/{userId}/read-all", 100L)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("All notifications marked as read"));
    }

    @Test
    @DisplayName("shouldReturnInternalServerError_whenMarkAllAsReadThrows")
    void shouldReturnInternalServerError_whenMarkAllAsReadThrows() throws Exception {
        // Arrange - service throws exception
        doThrow(new RuntimeException("DB error")).when(notificationService).markAllAsRead(500L);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/user/{userId}/read-all", 500L)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("shouldReturnHealthMessage_whenHealthCheckIsCalled")
    void shouldReturnHealthMessage_whenHealthCheckIsCalled() throws Exception {
        mockMvc.perform(get("/api/notifications/health")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification Service is running"));
    }
}
