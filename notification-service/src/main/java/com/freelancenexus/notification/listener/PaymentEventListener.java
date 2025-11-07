package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.PaymentEventDTO;
import com.freelancenexus.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.payment.completed}")
    public void handlePaymentCompleted(@Payload PaymentEventDTO event) {
        log.info("🔔 Received PAYMENT_COMPLETED event from RabbitMQ");
        log.info("Payment ID: {}, Amount: ${}, Transaction: {}, Project: {}", 
                event.getPaymentId(),
                event.getAmount(),
                event.getTransactionId(),
                event.getProjectTitle());
        
        try {
            notificationService.handlePaymentCompleted(event);
            log.info("✅ PAYMENT_COMPLETED event processed successfully");
        } catch (Exception e) {
            log.error("❌ Error processing PAYMENT_COMPLETED event: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}