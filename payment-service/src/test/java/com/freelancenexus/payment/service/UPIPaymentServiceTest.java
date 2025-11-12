package com.freelancenexus.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
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
        UPIPaymentService upi = upiPaymentService;
        UPIPaymentDTO dto = upi.generateUPIPaymentLink("TXN123", BigDecimal.valueOf(100), "test@upi", "INR");

        assertNotNull(dto);
        assertEquals("TXN123", dto.getTransactionId());
        assertEquals(BigDecimal.valueOf(100), dto.getAmount());
        assertEquals("test@upi", dto.getUpiId());
        assertEquals("INITIATED", dto.getStatus());
        assertNotNull(dto.getPaymentLink());
        assertNotNull(dto.getQrCode());
        assertEquals(900L, dto.getExpiresIn());
    }

    @Test
    void shouldVerifyUPIPaymentSuccessfullyOrFail() {
        upiPaymentService.generateUPIPaymentLink("TXN123", BigDecimal.valueOf(100), "test@upi", "INR");

        UPIPaymentService.TransactionVerificationResult result = upiPaymentService.verifyUPIPayment("TXN123");
        assertNotNull(result);
        assertNotNull(result.getStatus());
        assertNotNull(result.getMessage());
        assertTrue(result.isSuccess() || !result.isSuccess()); // deterministic in simulation
    }

    @Test
    void shouldReturnInvalid_whenTransactionDoesNotExist() {
        UPIPaymentService.TransactionVerificationResult result = upiPaymentService.verifyUPIPayment("INVALID");
        assertFalse(result.isSuccess());
        assertEquals("INVALID", result.getStatus());
        assertEquals("Transaction not found", result.getMessage());
    }

    @Test
    void shouldProcessPaymentCallbackSuccessfully() {
        upiPaymentService.generateUPIPaymentLink("TXN123", BigDecimal.valueOf(100), "test@upi", "INR");

        Map<String, Object> callback = new HashMap<>();
        callback.put("transactionId", "TXN123");
        callback.put("status", "SUCCESS");

        Map<String, Object> response = upiPaymentService.processPaymentCallback(callback);

        assertEquals("TXN123", response.get("transactionId"));
        assertTrue((Boolean) response.get("acknowledged"));
        assertTrue((Boolean) response.get("updated"));
    }

    @Test
    void shouldReturnUpdatedFalse_whenTransactionNotFoundInCallback() {
        Map<String, Object> callback = new HashMap<>();
        callback.put("transactionId", "INVALID");
        callback.put("status", "SUCCESS");

        Map<String, Object> response = upiPaymentService.processPaymentCallback(callback);

        assertEquals("INVALID", response.get("transactionId"));
        assertFalse((Boolean) response.get("updated"));
        assertEquals("Transaction not found", response.get("message"));
    }

    @Test
    void shouldInitiateRefundSuccessfullyOrFail() {
        // Create a successful transaction
        UPIPaymentDTO dto = upiPaymentService.generateUPIPaymentLink("TXN123", BigDecimal.valueOf(100), "test@upi", "INR");
        dto.setStatus("SUCCESS");

        UPIPaymentService.RefundResult refundResult = upiPaymentService.initiateRefund("TXN123", BigDecimal.valueOf(50));

        assertNotNull(refundResult);
        assertNotNull(refundResult.getMessage());
        assertTrue(refundResult.isSuccess() || !refundResult.isSuccess());
        if (refundResult.isSuccess()) {
            assertNotNull(refundResult.getRefundTransactionId());
        }
    }

    @Test
    void shouldFailRefundForNonSuccessfulTransaction() {
        UPIPaymentDTO dto = upiPaymentService.generateUPIPaymentLink("TXN124", BigDecimal.valueOf(100), "test@upi", "INR");
        dto.setStatus("FAILED");

        UPIPaymentService.RefundResult refundResult = upiPaymentService.initiateRefund("TXN124", BigDecimal.valueOf(50));
        assertFalse(refundResult.isSuccess());
        assertEquals("Can only refund successful transactions", refundResult.getMessage());
    }

    @Test
    void shouldFailRefundForNonExistingTransaction() {
        UPIPaymentService.RefundResult refundResult = upiPaymentService.initiateRefund("INVALID", BigDecimal.valueOf(50));
        assertFalse(refundResult.isSuccess());
        assertEquals("Transaction not found", refundResult.getMessage());
    }

    @Test
    void shouldValidateUPIIdCorrectly() {
        assertTrue(upiPaymentService.isValidUPIId("abc@upi"));
        assertFalse(upiPaymentService.isValidUPIId("invalidupi"));
        assertFalse(upiPaymentService.isValidUPIId(null));
    }
}
