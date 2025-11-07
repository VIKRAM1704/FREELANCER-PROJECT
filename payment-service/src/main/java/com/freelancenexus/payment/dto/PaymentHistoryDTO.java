package com.freelancenexus.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryDTO {
    
    private Long id;
    private Long paymentId;
    private String statusChange;
    private String notes;
    private LocalDateTime changedAt;
}