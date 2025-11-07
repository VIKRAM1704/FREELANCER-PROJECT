package com.freelancenexus.notification.listener;

import com.freelancenexus.notification.dto.ProposalEventDTO;
import com.freelancenexus.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProposalEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.proposal.submitted}")
    public void handleProposalSubmitted(@Payload ProposalEventDTO event) {
        log.info("🔔 Received PROPOSAL_SUBMITTED event from RabbitMQ");
        log.info("Proposal ID: {}, Project: {}, Freelancer: {}, Bid: ${}", 
                event.getProposalId(), 
                event.getProjectTitle(),
                event.getFreelancerName(),
                event.getBidAmount());
        
        try {
            notificationService.handleProposalSubmitted(event);
            log.info("✅ PROPOSAL_SUBMITTED event processed successfully");
        } catch (Exception e) {
            log.error("❌ Error processing PROPOSAL_SUBMITTED event: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger retry mechanism
        }
    }
}