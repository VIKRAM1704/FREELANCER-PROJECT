package com.freelancenexus.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.notification.dto.*;
import com.freelancenexus.notification.model.Notification;
import com.freelancenexus.notification.model.NotificationType;
import com.freelancenexus.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationService notificationService;

    private ProjectEventDTO projectEvent;
    private ProposalEventDTO proposalEvent;
    private PaymentEventDTO paymentEvent;

    @BeforeEach
    void setUp() throws Exception {
        // Common ObjectMapper mock for metadata
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"mocked\":\"metadata\"}");

        // Sample ProjectEventDTO
        projectEvent = new ProjectEventDTO(1L, 100L, "client@example.com",
                "Test Project", "Desc", 500.0, "OPEN", 200L,
                "Freelancer Name", "freelancer@example.com", LocalDateTime.now());

        // Sample ProposalEventDTO
        proposalEvent = new ProposalEventDTO(10L, 1L, "Test Project",
                200L, "Freelancer Name", "freelancer@example.com",
                100L, "client@example.com", 250.0, "SUBMITTED", LocalDateTime.now());

        // Sample PaymentEventDTO
        paymentEvent = new PaymentEventDTO(1000L, 1L, "Test Project",
                100L, "payer@example.com", 200L, "receiver@example.com",
                300.0, "USD", "COMPLETED", "TX123", LocalDateTime.now());
    }

    // ===================== Project Events =====================

    @Test
    void shouldHandleProjectCreatedSuccessfully() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> notificationService.handleProjectCreated(projectEvent));

        verify(notificationRepository, times(2)).save(any(Notification.class)); // before and after emailSent
        verify(emailService, times(1)).sendProjectCreatedEmail(projectEvent.getClientEmail(), projectEvent.getProjectTitle());
    }

    @Test
    void shouldThrowExceptionWhenProjectCreatedFails() {
        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("DB Error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.handleProjectCreated(projectEvent));
        assertTrue(exception.getMessage().contains("Notification processing failed"));
    }

    @Test
    void shouldHandleProjectAssignedSuccessfully() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> notificationService.handleProjectAssigned(projectEvent));

        verify(notificationRepository, times(2)).save(any(Notification.class)); // before and after emailSent
        verify(emailService, times(1)).sendProposalAcceptedEmail(projectEvent.getFreelancerEmail(), projectEvent.getProjectTitle());
    }

    @Test
    void shouldHandleProposalSubmittedSuccessfully() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> notificationService.handleProposalSubmitted(proposalEvent));

        verify(notificationRepository, times(3)).save(any(Notification.class)); // client + freelancer notifications
        verify(emailService, times(1)).sendProposalReceivedEmail(
                proposalEvent.getClientEmail(),
                proposalEvent.getFreelancerName(),
                proposalEvent.getProjectTitle()
        );
    }

    @Test
    void shouldHandlePaymentCompletedSuccessfully() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> notificationService.handlePaymentCompleted(paymentEvent));

        verify(notificationRepository, times(4)).save(any(Notification.class)); // payer + receiver, each before/after emailSent
        verify(emailService, times(1)).sendPaymentCompletedEmail(
                paymentEvent.getPayerEmail(),
                paymentEvent.getAmount(),
                paymentEvent.getTransactionId(),
                paymentEvent.getCurrency()
        );
        verify(emailService, times(1)).sendPaymentReceivedEmail(
                paymentEvent.getReceiverEmail(),
                paymentEvent.getAmount(),
                paymentEvent.getTransactionId(),
                paymentEvent.getProjectTitle(),
                paymentEvent.getCurrency()
        );
    }

    // ===================== Fetch Notifications =====================

    @Test
    void shouldReturnUserNotifications() {
        Notification notification = new Notification();
        notification.setUserId(100L);

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(100L))
                .thenReturn(Collections.singletonList(notification));

        var result = notificationService.getUserNotifications(100L);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getUserId());
    }

    @Test
    void shouldReturnUnreadNotifications() {
        Notification notification = new Notification();
        notification.setIsRead(false);
        notification.setUserId(100L);

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(100L))
                .thenReturn(Collections.singletonList(notification));

        var result = notificationService.getUnreadNotifications(100L);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
    }

    // ===================== Mark As Read =====================

    @Test
    void shouldMarkNotificationAsRead() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setIsRead(false);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        Notification result = notificationService.markAsRead(1L);
        assertTrue(result.getIsRead());
        assertNotNull(result.getReadAt());

        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void shouldThrowWhenNotificationNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(1L));
        assertTrue(exception.getMessage().contains("Notification not found"));
    }

    @Test
    void shouldMarkAllNotificationsAsRead() {
        Notification n1 = new Notification();
        n1.setIsRead(false);
        Notification n2 = new Notification();
        n2.setIsRead(false);

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(100L))
                .thenReturn(Arrays.asList(n1, n2));
        when(notificationRepository.saveAll(anyList())).thenReturn(Arrays.asList(n1, n2));

        notificationService.markAllAsRead(100L);

        assertTrue(n1.getIsRead());
        assertTrue(n2.getIsRead());
        assertNotNull(n1.getReadAt());
        assertNotNull(n2.getReadAt());
        verify(notificationRepository, times(1)).saveAll(anyList());
    }
}
