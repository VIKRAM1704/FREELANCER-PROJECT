package com.freelancenexus.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    @NotNull(message = "Payer ID is required")
    private Long payerId;
    
    @NotNull(message = "Payee ID is required")
    private Long payeeId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(max = 10)
    private String currency;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    @NotBlank(message = "UPI ID is required")
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+$", message = "Invalid UPI ID format")
    private String upiId;
    
    private String description;
}