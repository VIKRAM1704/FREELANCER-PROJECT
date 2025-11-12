package com.freelancenexus.notification.service;

import com.freelancenexus.notification.dto.NotificationDTO;
import com.freelancenexus.notification.model.Notification;
import com.freelancenexus.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private Logger log;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id("notif123")
                .userId("user1")
                .title("Test Title")
                .message("Test Message")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testSendNotification_Success() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId("user1");
        dto.setTitle("Hello");
        dto.setMessage("Welcome message");

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        doNothing().when(emailService).sendEmail(any(), any(), any());

        notificationService.sendNotification(dto);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emailService, times(1)).sendEmail(eq("user1"), eq("Hello"), eq("Welcome message"));
    }

    @Test
    void testSendNotification_RepositoryThrowsException() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId("user1");
        dto.setTitle("Error");
        dto.setMessage("Repo fails");

        when(notificationRepository.save(any(Notification.class)))
                .thenThrow(new RuntimeException("DB error"));

        // Should not throw — just log
        notificationService.sendNotification(dto);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void testGetNotificationsByUserId_Found() {
        when(notificationRepository.findByUserId("user1")).thenReturn(List.of(notification));

        List<Notification> result = notificationService.getNotificationsByUserId("user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("notif123");
        verify(notificationRepository, times(1)).findByUserId("user1");
    }

    @Test
    void testGetNotificationsByUserId_Empty() {
        when(notificationRepository.findByUserId("user2")).thenReturn(List.of());

        List<Notification> result = notificationService.getNotificationsByUserId("user2");

        assertThat(result).isEmpty();
        verify(notificationRepository, times(1)).findByUserId("user2");
    }

    @Test
    void testMarkAsRead_Found() {
        Notification unread = Notification.builder()
                .id("notif456")
                .userId("user2")
                .title("Old")
                .message("Old Message")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findById("notif456")).thenReturn(Optional.of(unread));
        when(notificationRepository.save(any(Notification.class))).thenReturn(unread);

        Notification updated = notificationService.markAsRead("notif456");

        assertThat(updated).isNotNull();
        assertThat(updated.isRead()).isTrue();
        verify(notificationRepository, times(1)).findById("notif456");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testMarkAsRead_NotFound() {
        when(notificationRepository.findById("missing")).thenReturn(Optional.empty());

        Notification result = notificationService.markAsRead("missing");

        assertThat(result).isNull();
        verify(notificationRepository, times(1)).findById("missing");
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
