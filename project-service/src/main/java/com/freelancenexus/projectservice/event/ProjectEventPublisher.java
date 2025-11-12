package com.freelancenexus.projectservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    private static final String EXCHANGE = "freelance-nexus-exchange";
    
    /**
     * Publish project created event
     */
    public void publishProjectCreatedEvent(Long projectId, Long clientId, String title, String category) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "PROJECT_CREATED");
            eventData.put("projectId", projectId);
            eventData.put("clientId", clientId);
            eventData.put("title", title);
            eventData.put("category", category);
            eventData.put("timestamp", LocalDateTime.now().toString());
            
            String routingKey = "project.created";
            
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, eventData);
            
            log.info("Published PROJECT_CREATED event for project: {} with routing key: {}", projectId, routingKey);
            
        } catch (Exception e) {
            log.error("Error publishing project created event for project {}: {}", projectId, e.getMessage());
        }
    }
    
    /**
     * Publish proposal submitted event
     */
    public void publishProposalSubmittedEvent(Long proposalId, Long projectId, Long freelancerId, String projectTitle) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "PROPOSAL_SUBMITTED");
            eventData.put("proposalId", proposalId);
            eventData.put("projectId", projectId);
            eventData.put("freelancerId", freelancerId);
            eventData.put("projectTitle", projectTitle);
            eventData.put("timestamp", LocalDateTime.now().toString());
            
            String routingKey = "proposal.submitted";
            
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, eventData);
            
            log.info("Published PROPOSAL_SUBMITTED event for proposal: {} on project: {} with routing key: {}", 
                     proposalId, projectId, routingKey);
            
        } catch (Exception e) {
            log.error("Error publishing proposal submitted event for proposal {}: {}", proposalId, e.getMessage());
        }
    }
    
    /**
     * Publish proposal accepted event
     */
    public void publishProposalAcceptedEvent(Long proposalId, Long projectId, Long freelancerId) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "PROPOSAL_ACCEPTED");
            eventData.put("proposalId", proposalId);
            eventData.put("projectId", projectId);
            eventData.put("freelancerId", freelancerId);
            eventData.put("timestamp", LocalDateTime.now().toString());
            
            String routingKey = "proposal.accepted";
            
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, eventData);
            
            log.info("Published PROPOSAL_ACCEPTED event for proposal: {} with routing key: {}", 
                     proposalId, routingKey);
            
        } catch (Exception e) {
            log.error("Error publishing proposal accepted event for proposal {}: {}", proposalId, e.getMessage());
        }
    }
    
    /**
     * Publish proposal rejected event
     */
    public void publishProposalRejectedEvent(Long proposalId, Long projectId, Long freelancerId) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "PROPOSAL_REJECTED");
            eventData.put("proposalId", proposalId);
            eventData.put("projectId", projectId);
            eventData.put("freelancerId", freelancerId);
            eventData.put("timestamp", LocalDateTime.now().toString());
            
            String routingKey = "proposal.rejected";
            
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, eventData);
            
            log.info("Published PROPOSAL_REJECTED event for proposal: {} with routing key: {}", 
                     proposalId, routingKey);
            
        } catch (Exception e) {
            log.error("Error publishing proposal rejected event for proposal {}: {}", proposalId, e.getMessage());
        }
    }
    
    /**
     * Publish project status changed event
     */
    public void publishProjectStatusChangedEvent(Long projectId, String oldStatus, String newStatus) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "PROJECT_STATUS_CHANGED");
            eventData.put("projectId", projectId);
            eventData.put("oldStatus", oldStatus);
            eventData.put("newStatus", newStatus);
            eventData.put("timestamp", LocalDateTime.now().toString());
            
            String routingKey = "project.status.changed";
            
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, eventData);
            
            log.info("Published PROJECT_STATUS_CHANGED event for project: {} from {} to {} with routing key: {}", 
                     projectId, oldStatus, newStatus, routingKey);
            
        } catch (Exception e) {
            log.error("Error publishing project status changed event for project {}: {}", projectId, e.getMessage());
        }
    }
    
    /**
     * Publish project assigned event
     */
    public void publishProjectAssignedEvent(Long projectId, Long freelancerId, Long clientId) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventType", "PROJECT_ASSIGNED");
            eventData.put("projectId", projectId);
            eventData.put("freelancerId", freelancerId);
            eventData.put("clientId", clientId);
            eventData.put("timestamp", LocalDateTime.now().toString());
            
            String routingKey = "project.assigned";
            
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, eventData);
            
            log.info("Published PROJECT_ASSIGNED event for project: {} to freelancer: {} with routing key: {}", 
                     projectId, freelancerId, routingKey);
            
        } catch (Exception e) {
            log.error("Error publishing project assigned event for project {}: {}", projectId, e.getMessage());
        }
    }
}