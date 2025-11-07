package com.freelancenexus.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UPIPaymentDTO {
    
    private String transactionId;
    private String upiId;
    private BigDecimal amount;
    private String currency;
    private String paymentLink;
    private String qrCode;
    private Long expiresIn; // seconds
    private String status;
    private String message;
}