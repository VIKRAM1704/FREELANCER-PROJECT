package com.freelancenexus.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String PROJECT_EXCHANGE = "project.exchange";
    public static final String PROPOSAL_EXCHANGE = "proposal.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // Queue names
    public static final String PROJECT_CREATED_QUEUE = "project.created.queue";
    public static final String PROJECT_ASSIGNED_QUEUE = "project.assigned.queue";
    public static final String PROPOSAL_SUBMITTED_QUEUE = "proposal.submitted.queue";
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.queue";

    // Routing keys
    public static final String PROJECT_CREATED_KEY = "project.created";
    public static final String PROJECT_ASSIGNED_KEY = "project.assigned";
    public static final String PROPOSAL_SUBMITTED_KEY = "proposal.submitted";
    public static final String PAYMENT_COMPLETED_KEY = "payment.completed";

    // Dead Letter Queue
    public static final String DLQ_EXCHANGE = "notification.dlq.exchange";
    public static final String DLQ_QUEUE = "notification.dlq.queue";
    public static final String DLQ_ROUTING_KEY = "notification.dlq";

    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        return retryTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        return factory;
    }

    // =============== PROJECT QUEUES ===============

    @Bean
    public TopicExchange projectExchange() {
        return new TopicExchange(PROJECT_EXCHANGE);
    }

    @Bean
    public Queue projectCreatedQueue() {
        return QueueBuilder.durable(PROJECT_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue projectAssignedQueue() {
        return QueueBuilder.durable(PROJECT_ASSIGNED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding projectCreatedBinding() {
        return BindingBuilder.bind(projectCreatedQueue())
                .to(projectExchange())
                .with(PROJECT_CREATED_KEY);
    }

    @Bean
    public Binding projectAssignedBinding() {
        return BindingBuilder.bind(projectAssignedQueue())
                .to(projectExchange())
                .with(PROJECT_ASSIGNED_KEY);
    }

    // =============== PROPOSAL QUEUES ===============

    @Bean
    public TopicExchange proposalExchange() {
        return new TopicExchange(PROPOSAL_EXCHANGE);
    }

    @Bean
    public Queue proposalSubmittedQueue() {
        return QueueBuilder.durable(PROPOSAL_SUBMITTED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding proposalSubmittedBinding() {
        return BindingBuilder.bind(proposalSubmittedQueue())
                .to(proposalExchange())
                .with(PROPOSAL_SUBMITTED_KEY);
    }

    // =============== PAYMENT QUEUES ===============

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable(PAYMENT_COMPLETED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding paymentCompletedBinding() {
        return BindingBuilder.bind(paymentCompletedQueue())
                .to(paymentExchange())
                .with(PAYMENT_COMPLETED_KEY);
    }

    // =============== DEAD LETTER QUEUE ===============

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue())
                .to(dlqExchange())
                .with(DLQ_ROUTING_KEY);
    }
}