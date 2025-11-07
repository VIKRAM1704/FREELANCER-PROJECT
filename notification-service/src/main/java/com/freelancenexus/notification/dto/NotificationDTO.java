package com.freelancenexus.notification.dto;

import com.freelancenexus.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// NotificationDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private Boolean emailSent;
    private String recipientEmail;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}





