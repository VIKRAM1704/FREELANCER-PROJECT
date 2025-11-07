package com.freelancenexus.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.notification.dto.*;
import com.freelancenexus.notification.model.Notification;
import com.freelancenexus.notification.model.NotificationType;
import com.freelancenexus.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handleProjectCreated(ProjectEventDTO event) {
        log.info("Processing PROJECT_CREATED event for project: {}", event.getProjectTitle());
        
        try {
            // Create notification for client
            Notification notification = new Notification();
            notification.setUserId(event.getClientId());
            notification.setType(NotificationType.PROJECT_CREATED);
            notification.setTitle("Project Posted Successfully");
            notification.setMessage(String.format(
                "Your project '%s' has been posted. Budget: $%.2f",
                event.getProjectTitle(),
                event.getBudget()
            ));
            notification.setRecipientEmail(event.getClientEmail());
            notification.setRelatedEntityType("PROJECT");
            notification.setRelatedEntityId(event.getProjectId());
            notification.setMetadata(createMetadata(event));
            
            notificationRepository.save(notification);
            
            // Send email
            emailService.sendProjectCreatedEmail(
                event.getClientEmail(),
                event.getProjectTitle()
            );
            
            notification.setEmailSent(true);
            notificationRepository.save(notification);
            
            log.info("✅ Successfully processed PROJECT_CREATED notification for user: {}", 
                    event.getClientId());
            
        } catch (Exception e) {
            log.error("❌ Failed to process PROJECT_CREATED event: {}", e.getMessage(), e);
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    @Transactional
    public void handleProjectAssigned(ProjectEventDTO event) {
        log.info("Processing PROJECT_ASSIGNED event for project: {}", event.getProjectTitle());
        
        try {
            // Notification for freelancer
            Notification freelancerNotification = new Notification();
            freelancerNotification.setUserId(event.getAssignedFreelancerId());
            freelancerNotification.setType(NotificationType.PROJECT_ASSIGNED);
            freelancerNotification.setTitle("Project Assigned to You");
            freelancerNotification.setMessage(String.format(
                "Congratulations! You've been assigned to project '%s'",
                event.getProjectTitle()
            ));
            freelancerNotification.setRecipientEmail(event.getFreelancerEmail());
            freelancerNotification.setRelatedEntityType("PROJECT");
            freelancerNotification.setRelatedEntityId(event.getProjectId());
            freelancerNotification.setMetadata(createMetadata(event));
            
            notificationRepository.save(freelancerNotification);
            
            // Send email to freelancer
            emailService.sendProposalAcceptedEmail(
                event.getFreelancerEmail(),
                event.getProjectTitle()
            );
            
            freelancerNotification.setEmailSent(true);
            notificationRepository.save(freelancerNotification);
            
            log.info("✅ Successfully processed PROJECT_ASSIGNED notification for freelancer: {}", 
                    event.getAssignedFreelancerId());
            
        } catch (Exception e) {
            log.error("❌ Failed to process PROJECT_ASSIGNED event: {}", e.getMessage(), e);
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    @Transactional
    public void handleProposalSubmitted(ProposalEventDTO event) {
        log.info("Processing PROPOSAL_SUBMITTED event for project: {}", event.getProjectTitle());
        
        try {
            // Notification for client
            Notification clientNotification = new Notification();
            clientNotification.setUserId(event.getClientId());
            clientNotification.setType(NotificationType.PROPOSAL_SUBMITTED);
            clientNotification.setTitle("New Proposal Received");
            clientNotification.setMessage(String.format(
                "%s submitted a proposal for '%s' with bid amount: $%.2f",
                event.getFreelancerName(),
                event.getProjectTitle(),
                event.getBidAmount()
            ));
            clientNotification.setRecipientEmail(event.getClientEmail());
            clientNotification.setRelatedEntityType("PROPOSAL");
            clientNotification.setRelatedEntityId(event.getProposalId());
            clientNotification.setMetadata(createMetadata(event));
            
            notificationRepository.save(clientNotification);
            
            // Send email to client
            emailService.sendProposalReceivedEmail(
                event.getClientEmail(),
                event.getFreelancerName(),
                event.getProjectTitle()
            );
            
            clientNotification.setEmailSent(true);
            notificationRepository.save(clientNotification);
            
            // Notification for freelancer (confirmation)
            Notification freelancerNotification = new Notification();
            freelancerNotification.setUserId(event.getFreelancerId());
            freelancerNotification.setType(NotificationType.PROPOSAL_SUBMITTED);
            freelancerNotification.setTitle("Proposal Submitted Successfully");
            freelancerNotification.setMessage(String.format(
                "Your proposal for '%s' has been submitted successfully",
                event.getProjectTitle()
            ));
            freelancerNotification.setRecipientEmail(event.getFreelancerEmail());
            freelancerNotification.setRelatedEntityType("PROPOSAL");
            freelancerNotification.setRelatedEntityId(event.getProposalId());
            freelancerNotification.setMetadata(createMetadata(event));
            
            notificationRepository.save(freelancerNotification);
            
            log.info("✅ Successfully processed PROPOSAL_SUBMITTED notification");
            
        } catch (Exception e) {
            log.error("❌ Failed to process PROPOSAL_SUBMITTED event: {}", e.getMessage(), e);
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    @Transactional
    public void handlePaymentCompleted(PaymentEventDTO event) {
        log.info("Processing PAYMENT_COMPLETED event for transaction: {}", event.getTransactionId());
        
        try {
            // Notification for payer
            Notification payerNotification = new Notification();
            payerNotification.setUserId(event.getPayerId());
            payerNotification.setType(NotificationType.PAYMENT_COMPLETED);
            payerNotification.setTitle("Payment Completed");
            payerNotification.setMessage(String.format(
                "Your payment of $%.2f for project '%s' has been processed successfully",
                event.getAmount(),
                event.getProjectTitle()
            ));
            payerNotification.setRecipientEmail(event.getPayerEmail());
            payerNotification.setRelatedEntityType("PAYMENT");
            payerNotification.setRelatedEntityId(event.getPaymentId());
            payerNotification.setMetadata(createMetadata(event));
            
            notificationRepository.save(payerNotification);
            
            // Send email to payer
            emailService.sendPaymentCompletedEmail(
                event.getPayerEmail(),
                event.getAmount(),
                event.getTransactionId(),
                event.getCurrency()
            );
            
            payerNotification.setEmailSent(true);
            notificationRepository.save(payerNotification);
            
            // Notification for receiver
            Notification receiverNotification = new Notification();
            receiverNotification.setUserId(event.getReceiverId());
            receiverNotification.setType(NotificationType.PAYMENT_COMPLETED);
            receiverNotification.setTitle("Payment Received");
            receiverNotification.setMessage(String.format(
                "You've received a payment of $%.2f for project '%s'",
                event.getAmount(),
                event.getProjectTitle()
            ));
            receiverNotification.setRecipientEmail(event.getReceiverEmail());
            receiverNotification.setRelatedEntityType("PAYMENT");
            receiverNotification.setRelatedEntityId(event.getPaymentId());
            receiverNotification.setMetadata(createMetadata(event));
            
            notificationRepository.save(receiverNotification);
            
            // Send email to receiver
            emailService.sendPaymentReceivedEmail(
                event.getReceiverEmail(),
                event.getAmount(),
                event.getTransactionId(),
                event.getProjectTitle(),
                event.getCurrency()
            );
            
            receiverNotification.setEmailSent(true);
            notificationRepository.save(receiverNotification);
            
            log.info("✅ Successfully processed PAYMENT_COMPLETED notification");
            
        } catch (Exception e) {
            log.error("❌ Failed to process PAYMENT_COMPLETED event: {}", e.getMessage(), e);
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
    }

    private String createMetadata(Object event) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("timestamp", LocalDateTime.now().toString());
            metadata.put("eventData", event);
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            log.error("Failed to create metadata: {}", e.getMessage());
            return "{}";
        }
    }
}