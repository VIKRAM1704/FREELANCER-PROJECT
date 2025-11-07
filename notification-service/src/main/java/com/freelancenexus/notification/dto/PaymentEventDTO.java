package com.freelancenexus.notification.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//PaymentEventDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDTO {
 private Long paymentId;
 private Long projectId;
 private String projectTitle;
 private Long payerId;
 private String payerEmail;
 private Long receiverId;
 private String receiverEmail;
 private Double amount;
 private String currency;
 private String status;
 private String transactionId;
 private LocalDateTime completedAt;
}