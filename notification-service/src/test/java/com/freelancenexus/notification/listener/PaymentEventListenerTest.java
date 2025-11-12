package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.NotificationDTO;
import com.freelancenexus.notification.event.PaymentCompletedEvent;
import com.freelancenexus.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Logger log;

    @InjectMocks
    private PaymentEventListener paymentEventListener;

    private PaymentCompletedEvent paymentEvent;

    @BeforeEach
    void setUp() {
        paymentEvent = new PaymentCompletedEvent(this, "freelancer123", "Cool Project", 2500.00);
    }

    @Test
    void testHandlePaymentCompleted_Success() {
        doNothing().when(notificationService).sendNotification(any(NotificationDTO.class));

        paymentEventListener.handlePaymentCompleted(paymentEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }

    @Test
    void testHandlePaymentCompleted_ExceptionHandled() {
        doThrow(new RuntimeException("Notification failed"))
                .when(notificationService).sendNotification(any(NotificationDTO.class));

        paymentEventListener.handlePaymentCompleted(paymentEvent);

        verify(notificationService, times(1)).sendNotification(any(NotificationDTO.class));
    }
}
