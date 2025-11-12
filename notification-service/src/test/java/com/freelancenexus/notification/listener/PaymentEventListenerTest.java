package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.PaymentEventDTO;
import com.freelancenexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentEventListener listener;

    private PaymentEventDTO paymentEvent;

    @BeforeEach
    void setUp() {
        paymentEvent = new PaymentEventDTO();
        paymentEvent.setPaymentId(1000L);
        paymentEvent.setProjectId(1L);
        paymentEvent.setProjectTitle("Test Project");
        paymentEvent.setPayerId(100L);
        paymentEvent.setPayerEmail("payer@example.com");
        paymentEvent.setReceiverId(200L);
        paymentEvent.setReceiverEmail("receiver@example.com");
        paymentEvent.setAmount(300.0);
        paymentEvent.setCurrency("USD");
        paymentEvent.setStatus("COMPLETED");
        paymentEvent.setTransactionId("TX123");
    }

    @Test
    void shouldHandlePaymentCompletedEvent() {
        doNothing().when(notificationService).handlePaymentCompleted(paymentEvent);

        listener.handlePaymentCompleted(paymentEvent);

        verify(notificationService, times(1)).handlePaymentCompleted(paymentEvent);
    }

    @Test
    void shouldThrowWhenPaymentCompletedFails() {
        doThrow(new RuntimeException("Service failure"))
                .when(notificationService).handlePaymentCompleted(paymentEvent);

        RuntimeException exception = null;
        try {
            listener.handlePaymentCompleted(paymentEvent);
        } catch (RuntimeException e) {
            exception = e;
        }

        assert exception != null;
        assert exception.getMessage().equals("Service failure");
        verify(notificationService, times(1)).handlePaymentCompleted(paymentEvent);
    }
}
