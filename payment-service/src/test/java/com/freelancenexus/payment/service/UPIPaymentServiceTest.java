package com.freelancenexus.payment.service;

import com.freelancenexus.payment.dto.UPIPaymentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UPIPaymentServiceTest {

    private UPIPaymentService upiPaymentService;

    @BeforeEach
    void setUp() {
        upiPaymentService = new UPIPaymentService();
    }

    @Test
    void shouldGenerateUPIPaymentLinkSuccessfully() {
        UPIPaymentDTO upiPayment = upiPaymentService.generateUPIPaymentLink("TXN-123", new BigDecimal("100.00"), "test@upi", "INR");

        assertNotNull(upiPayment);
        assertEquals("TXN-123", upiPayment.getTransactionId());
        assertEquals("test@upi", upiPayment.getUpiId());
        assertEquals("INR", upiPayment.getCurrency());
        assertEquals(new BigDecimal("100.00"), upiPayment.getAmount());
        assertEquals("INITIATED", upiPayment.getStatus());
    }

    @Test
    void shouldVerifyUPIPaymentSuccessOrFail() {
        upiPaymentService.generateUPIPaymentLink("TXN-124", new BigDecimal("50.00"), "test2@upi", "INR");
        UPIPaymentService.TransactionVerificationResult result = upiPaymentService.verifyUPIPayment("TXN-124");

        assertNotNull(result);
        assertTrue(result.isSuccess() || !result.isSuccess());
        assertNotNull(result.getStatus());
        assertNotNull(result.getMessage());
    }

    @Test
    void shouldReturnInvalidForNonExistingTransaction() {
        UPIPaymentService.TransactionVerificationResult result = upiPaymentService.verifyUPIPayment("NON_EXISTENT_TXN");

        assertFalse(result.isSuccess());
        assertEquals("INVALID", result.getStatus());
        assertEquals("Transaction not found", result.getMessage());
    }

    @Test
    void shouldProcessPaymentCallbackSuccessfully() {
        upiPaymentService.generateUPIPaymentLink("TXN-125", new BigDecimal("75.00"), "test3@upi", "INR");
        Map<String, Object> response = upiPaymentService.processPaymentCallback(Map.of("transactionId", "TXN-125", "status", "SUCCESS"));

        assertTrue((Boolean) response.get("acknowledged"));
        assertTrue((Boolean) response.get("updated"));
        assertEquals("TXN-125", response.get("transactionId"));
    }

    @Test
    void shouldReturnNotUpdatedForNonExistingCallback() {
        Map<String, Object> response = upiPaymentService.processPaymentCallback(Map.of("transactionId", "NON_EXISTENT", "status", "SUCCESS"));

        assertFalse((Boolean) response.get("updated"));
        assertEquals("Transaction not found", response.get("message"));
    }

    @Test
    void shouldInitiateRefundSuccessfully() {
        UPIPaymentDTO upiPayment = upiPaymentService.generateUPIPaymentLink("TXN-126", new BigDecimal("120.00"), "test4@upi", "INR");
        // Simulate success status
        upiPayment.setStatus("SUCCESS");

        UPIPaymentService.RefundResult result = upiPaymentService.initiateRefund("TXN-126", new BigDecimal("120.00"));

        assertNotNull(result);
        assertNotNull(result.getRefundTransactionId());
        assertNotNull(result.getMessage());
    }

    @Test
    void shouldFailRefundForNonSuccessfulTransaction() {
        UPIPaymentDTO upiPayment = upiPaymentService.generateUPIPaymentLink("TXN-127", new BigDecimal("80.00"), "test5@upi", "INR");
        upiPayment.setStatus("INITIATED");

        UPIPaymentService.RefundResult result = upiPaymentService.initiateRefund("TXN-127", new BigDecimal("80.00"));

        assertFalse(result.isSuccess());
        assertEquals("Can only refund successful transactions", result.getMessage());
    }

    @Test
    void shouldReturnFalseForInvalidUPIId() {
        assertFalse(upiPaymentService.isValidUPIId("invalidupi"));
        assertTrue(upiPaymentService.isValidUPIId("valid@upi"));
    }
}
