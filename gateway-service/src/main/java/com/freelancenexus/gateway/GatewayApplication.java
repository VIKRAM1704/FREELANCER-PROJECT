package com.freelancenexus.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * 
 * This is the entry point for all client requests to Freelance Nexus microservices.
 * The gateway provides:
 * - Centralized routing to microservices
 * - Authentication and authorization
 * - Rate limiting
 * - Circuit breaker patterns
 * - Request/response logging
 * - CORS handling
 * - Load balancing
 * 
 * Key Features:
 * - Routes requests to appropriate microservices
 * - Validates JWT tokens from Keycloak
 * - Implements security policies
 * - Provides fault tolerance through circuit breakers
 * - Monitors and logs all API traffic
 * 
 * @EnableDiscoveryClient: Enables service discovery via Eureka
 * 
 * Gateway URL: http://localhost:8765
 */

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("==============================================");
        System.out.println("API Gateway started successfully!");
        System.out.println("Gateway URL: http://localhost:8765");
        System.out.println("==============================================");
    }
}
