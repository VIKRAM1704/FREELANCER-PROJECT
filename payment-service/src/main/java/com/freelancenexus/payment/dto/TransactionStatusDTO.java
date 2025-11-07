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
public class TransactionStatusDTO {
    
    private String transactionId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private String message;
}