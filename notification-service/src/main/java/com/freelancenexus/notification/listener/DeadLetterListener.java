package com.freelancenexus.notification.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterListener {

    @RabbitListener(queues = "${rabbitmq.queue.dlq}")
    public void handleDLQMessage(String message) {
        log.error("DLQ message received: {}", message);
    }
}
