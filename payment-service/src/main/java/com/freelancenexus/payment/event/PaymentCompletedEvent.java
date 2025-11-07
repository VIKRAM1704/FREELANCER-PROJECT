package com.freelancenexus.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long paymentId;
    private String transactionId;
    private Long projectId;
    private Long payerId;
    private Long payeeId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paymentDate;
}