package com.freelancenexus.payment.service;

import com.freelancenexus.payment.dto.UPIPaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UPIPaymentService {
    
    // Simulated UPI transaction storage
    private final Map<String, UPIPaymentDTO> upiTransactions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    
    /**
     * Generate UPI payment link for a transaction
     */
    public UPIPaymentDTO generateUPIPaymentLink(String transactionId, BigDecimal amount, String upiId, String currency) {
        log.info("Generating UPI payment link for transaction: {}", transactionId);
        
        // Generate simulated UPI payment link
        String paymentLink = String.format("upi://pay?pa=%s&pn=FreelanceNexus&am=%s&cu=%s&tn=%s",
                upiId, amount.toString(), currency, transactionId);
        
        // Generate simulated QR code (base64 encoded string)
        String qrCode = generateSimulatedQRCode(paymentLink);
        
        UPIPaymentDTO upiPayment = UPIPaymentDTO.builder()
                .transactionId(transactionId)
                .upiId(upiId)
                .amount(amount)
                .currency(currency)
                .paymentLink(paymentLink)
                .qrCode(qrCode)
                .expiresIn(900L) // 15 minutes
                .status("INITIATED")
                .message("UPI payment link generated successfully")
                .build();
        
        // Store in simulated transaction storage
        upiTransactions.put(transactionId, upiPayment);
        
        log.info("UPI payment link generated for transaction: {}", transactionId);
        return upiPayment;
    }
    
    /**
     * Verify UPI payment transaction
     * Simulates checking with UPI gateway
     */
    public TransactionVerificationResult verifyUPIPayment(String transactionId) {
        log.info("Verifying UPI payment for transaction: {}", transactionId);
        
        UPIPaymentDTO upiTransaction = upiTransactions.get(transactionId);
        
        if (upiTransaction == null) {
            log.warn("UPI transaction not found: {}", transactionId);
            return new TransactionVerificationResult(false, "INVALID", "Transaction not found");
        }
        
        // Simulate UPI payment verification (90% success rate)
        boolean isSuccessful = random.nextInt(100) < 90;
        
        String status = isSuccessful ? "SUCCESS" : "FAILED";
        String message = isSuccessful ? 
                "Payment verified successfully" : 
                "Payment verification failed - Insufficient funds or user cancelled";
        
        upiTransaction.setStatus(status);
        upiTransaction.setMessage(message);
        
        log.info("UPI payment verification result for {}: {}", transactionId, status);
        
        return new TransactionVerificationResult(isSuccessful, status, message);
    }
    
    /**
     * Process UPI payment callback
     * Simulates webhook from UPI gateway
     */
    public Map<String, Object> processPaymentCallback(Map<String, Object> callbackData) {
        log.info("Processing UPI payment callback");
        
        String transactionId = (String) callbackData.get("transactionId");
        String status = (String) callbackData.get("status");
        
        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", transactionId);
        response.put("acknowledged", true);
        response.put("timestamp", System.currentTimeMillis());
        
        UPIPaymentDTO upiTransaction = upiTransactions.get(transactionId);
        if (upiTransaction != null) {
            upiTransaction.setStatus(status);
            response.put("updated", true);
        } else {
            response.put("updated", false);
            response.put("message", "Transaction not found");
        }
        
        log.info("Callback processed for transaction: {}", transactionId);
        return response;
    }
    
    /**
     * Initiate UPI refund
     */
    public RefundResult initiateRefund(String transactionId, BigDecimal amount) {
        log.info("Initiating UPI refund for transaction: {}", transactionId);
        
        UPIPaymentDTO upiTransaction = upiTransactions.get(transactionId);
        
        if (upiTransaction == null) {
            return new RefundResult(false, null, "Transaction not found");
        }
        
        if (!"SUCCESS".equals(upiTransaction.getStatus())) {
            return new RefundResult(false, null, "Can only refund successful transactions");
        }
        
        // Generate refund transaction ID
        String refundTransactionId = "RFD-" + UUID.randomUUID().toString();
        
        // Simulate refund processing (95% success rate)
        boolean isSuccessful = random.nextInt(100) < 95;
        
        String message = isSuccessful ? 
                "Refund initiated successfully. Amount will be credited within 5-7 business days" :
                "Refund initiation failed. Please try again later";
        
        log.info("Refund {} for transaction: {}", isSuccessful ? "successful" : "failed", transactionId);
        
        return new RefundResult(isSuccessful, refundTransactionId, message);
    }
    
    /**
     * Generate simulated QR code
     */
    private String generateSimulatedQRCode(String data) {
        // In production, use actual QR code library
        // This simulates a base64 encoded QR code
        byte[] qrBytes = new byte[256];
        random.nextBytes(qrBytes);
        return Base64.getEncoder().encodeToString(qrBytes);
    }
    
    /**
     * Check if UPI ID is valid (simulated)
     */
    public boolean isValidUPIId(String upiId) {
        return upiId != null && upiId.matches("^[\\w.-]+@[\\w.-]+$");
    }
    
    // Inner classes for results
    
    public static class TransactionVerificationResult {
        private final boolean success;
        private final String status;
        private final String message;
        
        public TransactionVerificationResult(boolean success, String status, String message) {
            this.success = success;
            this.status = status;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getStatus() {
            return status;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    public static class RefundResult {
        private final boolean success;
        private final String refundTransactionId;
        private final String message;
        
        public RefundResult(boolean success, String refundTransactionId, String message) {
            this.success = success;
            this.refundTransactionId = refundTransactionId;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getRefundTransactionId() {
            return refundTransactionId;
        }
        
        public String getMessage() {
            return message;
        }
    }
}