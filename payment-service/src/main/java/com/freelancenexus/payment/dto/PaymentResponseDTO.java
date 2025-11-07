package com.freelancenexus.payment.dto;

import com.freelancenexus.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    
    private Long id;
    private String transactionId;
    private Long projectId;
    private Long payerId;
    private Long payeeId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String upiId;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}