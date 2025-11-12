package com.freelancenexus.payment.controller;

import com.freelancenexus.payment.dto.*;
import com.freelancenexus.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    // Only clients can initiate payments
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        log.info("POST /api/payments/initiate - Initiating payment for project: {}", request.getProjectId());
        
        try {
            PaymentResponseDTO response = paymentService.initiatePayment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate payment: " + e.getMessage());
        }
    }
    
    // Both client and freelancer can verify payments
    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<TransactionStatusDTO> verifyPayment(
            @RequestParam String transactionId) {
        log.info("POST /api/payments/verify - Verifying transaction: {}", transactionId);
        
        try {
            TransactionStatusDTO status = paymentService.verifyPayment(transactionId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
    
    // Both can view payment details
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        log.info("GET /api/payments/{} - Fetching payment", id);
        
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            log.error("Payment not found: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        log.info("GET /api/payments/transaction/{} - Fetching payment", transactionId);
        
        try {
            PaymentResponseDTO payment = paymentService.getPaymentByTransactionId(transactionId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            log.error("Payment not found: {}", transactionId);
            return ResponseEntity.notFound().build();
        }
    }
    
    // Both can view project payments
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByProject(
            @PathVariable Long projectId) {
        log.info("GET /api/payments/project/{} - Fetching project payments", projectId);
        
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByProject(projectId);
        return ResponseEntity.ok(payments);
    }
    
    // Users can view their own payment history
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<PaymentResponseDTO>> getUserPaymentHistory(
            @PathVariable Long userId) {
        log.info("GET /api/payments/user/{} - Fetching user payment history", userId);
        
        List<PaymentResponseDTO> payments = paymentService.getUserPaymentHistory(userId);
        return ResponseEntity.ok(payments);
    }
    
    // Only clients can refund payments
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        log.info("POST /api/payments/{}/refund - Initiating refund", id);
        
        String reason = request.getOrDefault("reason", "Customer request");
        
        try {
            PaymentResponseDTO payment = paymentService.refundPayment(id, reason);
            return ResponseEntity.ok(payment);
        } catch (IllegalStateException e) {
            log.error("Cannot refund payment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error refunding payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refund payment: " + e.getMessage());
        }
    }
    
    @GetMapping("/{paymentId}/history")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<PaymentHistoryDTO>> getTransactionHistory(
            @PathVariable Long paymentId) {
        log.info("GET /api/payments/{}/history - Fetching transaction history", paymentId);
        
        List<PaymentHistoryDTO> history = paymentService.getTransactionHistory(paymentId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<List<PaymentHistoryDTO>> getUserTransactionHistory(
            @PathVariable Long userId) {
        log.info("GET /api/payments/history/{} - Fetching user transaction history", userId);
        
        List<PaymentHistoryDTO> history = paymentService.getUserTransactionHistory(userId);
        return ResponseEntity.ok(history);
    }
    
    // Public health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Payment Service",
                "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}