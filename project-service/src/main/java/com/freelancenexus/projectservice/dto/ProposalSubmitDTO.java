package com.freelancenexus.projectservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalSubmitDTO {

    @NotNull(message = "Freelancer ID is required")
    private Long freelancerId;

    @NotBlank(message = "Cover letter is required")
    @Size(min = 50, message = "Cover letter must be at least 50 characters")
    private String coverLetter;

    @NotNull(message = "Proposed budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Proposed budget must be greater than 0")
    private BigDecimal proposedBudget;

    @NotNull(message = "Delivery days is required")
    @Min(value = 1, message = "Delivery days must be at least 1")
    private Integer deliveryDays;
}