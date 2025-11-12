package com.freelancenexus.notification.controller;

import com.freelancenexus.notification.dto.NotificationDTO;
import com.freelancenexus.notification.model.Notification;
import com.freelancenexus.notification.model.NotificationType;
import com.freelancenexus.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();

        notification1 = new Notification();
        notification1.setId(1L);
        notification1.setUserId(100L);
        notification1.setType(NotificationType.INFO);
        notification1.setTitle("Test Notification 1");
        notification1.setMessage("Message 1");
        notification1.setIsRead(false);
        notification1.setEmailSent(true);
        notification1.setRecipientEmail("user@example.com");
        notification1.setCreatedAt(LocalDateTime.now());

        notification2 = new Notification();
        notification2.setId(2L);
        notification2.setUserId(100L);
        notification2.setType(NotificationType.ALERT);
        notification2.setTitle("Test Notification 2");
        notification2.setMessage("Message 2");
        notification2.setIsRead(true);
        notification2.setEmailSent(false);
        notification2.setRecipientEmail("user@example.com");
        notification2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldReturnUserNotifications() throws Exception {
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationService.getUserNotifications(100L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(notificationService, times(1)).getUserNotifications(100L);
    }

    @Test
    void shouldReturnEmptyUserNotifications() throws Exception {
        when(notificationService.getUserNotifications(101L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications/user/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));

        verify(notificationService, times(1)).getUserNotifications(101L);
    }

    @Test
    void shouldReturnUnreadNotifications() throws Exception {
        List<Notification> unread = Collections.singletonList(notification1);
        when(notificationService.getUnreadNotifications(100L)).thenReturn(unread);

        mockMvc.perform(get("/api/notifications/user/100/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(notificationService, times(1)).getUnreadNotifications(100L);
    }

    @Test
    void shouldReturnUnreadCount() throws Exception {
        List<Notification> unread = Collections.singletonList(notification1);
        when(notificationService.getUnreadNotifications(100L)).thenReturn(unread);

        mockMvc.perform(get("/api/notifications/user/100/unread/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(notificationService, times(1)).getUnreadNotifications(100L);
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        when(notificationService.markAsRead(1L)).thenReturn(notification1);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.isRead", is(false))); // original value is false

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    void shouldMarkAllNotificationsAsRead() throws Exception {
        doNothing().when(notificationService).markAllAsRead(100L);

        mockMvc.perform(put("/api/notifications/user/100/read-all"))
                .andExpect(status().isOk())
                .andExpect(content().string("All notifications marked as read"));

        verify(notificationService, times(1)).markAllAsRead(100L);
    }

    @Test
    void shouldReturnHealthCheckMessage() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification Service is running"));
    }

    @Test
    void shouldHandleServiceExceptionGracefully() throws Exception {
        when(notificationService.getUserNotifications(100L)).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/api/notifications/user/100"))
                .andExpect(status().isInternalServerError());

        verify(notificationService, times(1)).getUserNotifications(100L);
    }
}
