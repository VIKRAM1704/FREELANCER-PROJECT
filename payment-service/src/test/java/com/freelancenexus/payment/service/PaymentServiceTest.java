package com.freelancenexus.payment.service;

import com.freelancenexus.payment.dto.*;
import com.freelancenexus.payment.model.Payment;
import com.freelancenexus.payment.model.PaymentStatus;
import com.freelancenexus.payment.model.TransactionHistory;
import com.freelancenexus.payment.repository.PaymentRepository;
import com.freelancenexus.payment.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private UPIPaymentService upiPaymentService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldInitiatePaymentSuccessfully() {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .projectId(1L).payerId(2L).payeeId(3L)
                .amount(new BigDecimal("100")).currency("INR")
                .paymentMethod("UPI").upiId("test@upi").build();

        when(upiPaymentService.isValidUPIId("test@upi")).thenReturn(true);
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(upiPaymentService.generateUPIPaymentLink(anyString(), any(), anyString(), anyString()))
                .thenReturn(UPIPaymentDTO.builder().transactionId("TXN-1").build());

        PaymentResponseDTO response = paymentService.initiatePayment(request);

        assertNotNull(response);
        assertEquals("TXN-1", response.getTransactionId());
        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
    }

    @Test
    void shouldThrowExceptionForInvalidUPI() {
        PaymentRequestDTO request = PaymentRequestDTO.builder().upiId("invalidupi").build();
        when(upiPaymentService.isValidUPIId("invalidupi")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> paymentService.initiatePayment(request));
    }

    @Test
    void shouldVerifyPaymentCompleted() {
        Payment payment = Payment.builder().transactionId("TXN-1").status(PaymentStatus.PENDING)
                .amount(new BigDecimal("50")).currency("INR").build();
        when(paymentRepository.findByTransactionId("TXN-1")).thenReturn(Optional.of(payment));
        when(upiPaymentService.verifyUPIPayment("TXN-1"))
                .thenReturn(new UPIPaymentService.TransactionVerificationResult(true, "SUCCESS", "ok"));

        TransactionStatusDTO status = paymentService.verifyPayment("TXN-1");

        assertEquals(PaymentStatus.COMPLETED, status.getStatus());
        verify(paymentRepository).save(payment);
        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
    }

    @Test
    void shouldVerifyPaymentFailed() {
        Payment payment = Payment.builder().transactionId("TXN-2").status(PaymentStatus.PENDING)
                .amount(new BigDecimal("50")).currency("INR").build();
        when(paymentRepository.findByTransactionId("TXN-2")).thenReturn(Optional.of(payment));
        when(upiPaymentService.verifyUPIPayment("TXN-2"))
                .thenReturn(new UPIPaymentService.TransactionVerificationResult(false, "FAILED", "Insufficient"));

        TransactionStatusDTO status = paymentService.verifyPayment("TXN-2");

        assertEquals(PaymentStatus.FAILED, status.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void shouldRefundPaymentSuccessfully() {
        Payment payment = Payment.builder().id(1L).transactionId("TXN-3").status(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("75")).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(upiPaymentService.initiateRefund("TXN-3", payment.getAmount()))
                .thenReturn(new UPIPaymentService.RefundResult(true, "RFD-1", "success"));
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentResponseDTO response = paymentService.refundPayment(1L, "Customer request");

        assertEquals(PaymentStatus.REFUNDED, response.getStatus());
        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
    }

    @Test
    void shouldThrowExceptionWhenRefundNonCompletedPayment() {
        Payment payment = Payment.builder().id(2L).status(PaymentStatus.PENDING).build();
        when(paymentRepository.findById(2L)).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class, () -> paymentService.refundPayment(2L, "reason"));
    }
}
