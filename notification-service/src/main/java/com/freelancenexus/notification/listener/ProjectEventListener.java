package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.ProjectEventDTO;
import com.freelancenexus.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.project.created}")
    public void handleProjectCreated(@Payload ProjectEventDTO event) {
        log.info("🔔 Received PROJECT_CREATED event from RabbitMQ");
        log.info("Project ID: {}, Title: {}, Client: {}", 
                event.getProjectId(), 
                event.getProjectTitle(), 
                event.getClientEmail());
        
        try {
            notificationService.handleProjectCreated(event);
            log.info("✅ PROJECT_CREATED event processed successfully");
        } catch (Exception e) {
            log.error("❌ Error processing PROJECT_CREATED event: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.project.assigned}")
    public void handleProjectAssigned(@Payload ProjectEventDTO event) {
        log.info("🔔 Received PROJECT_ASSIGNED event from RabbitMQ");
        log.info("Project ID: {}, Title: {}, Assigned to: {}", 
                event.getProjectId(), 
                event.getProjectTitle(), 
                event.getFreelancerEmail());
        
        try {
            notificationService.handleProjectAssigned(event);
            log.info("✅ PROJECT_ASSIGNED event processed successfully");
        } catch (Exception e) {
            log.error("❌ Error processing PROJECT_ASSIGNED event: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}