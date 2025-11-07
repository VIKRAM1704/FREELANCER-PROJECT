package com.freelancenexus.projectservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.project}")
    private String projectExchange;

    @Value("${rabbitmq.queue.project.created}")
    private String projectCreatedQueue;

    @Value("${rabbitmq.queue.proposal.submitted}")
    private String proposalSubmittedQueue;

    @Value("${rabbitmq.routing.project.created}")
    private String projectCreatedRoutingKey;

    @Value("${rabbitmq.routing.proposal.submitted}")
    private String proposalSubmittedRoutingKey;

    @Bean
    public TopicExchange projectExchange() {
        return new TopicExchange(projectExchange);
    }

    @Bean
    public Queue projectCreatedQueue() {
        return new Queue(projectCreatedQueue, true);
    }

    @Bean
    public Queue proposalSubmittedQueue() {
        return new Queue(proposalSubmittedQueue, true);
    }

    @Bean
    public Binding projectCreatedBinding() {
        return BindingBuilder
                .bind(projectCreatedQueue())
                .to(projectExchange())
                .with(projectCreatedRoutingKey);
    }

    @Bean
    public Binding proposalSubmittedBinding() {
        return BindingBuilder
                .bind(proposalSubmittedQueue())
                .to(projectExchange())
                .with(proposalSubmittedRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}