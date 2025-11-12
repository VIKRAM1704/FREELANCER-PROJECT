package com.freelancenexus.gateway.config;

import com.freelancenexus.gateway.filter.AuthenticationFilter;
import com.freelancenexus.gateway.filter.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration
 * 
 * This class defines all routes from the API Gateway to microservices.
 * Each route includes:
 * - Service ID for load balancing via Eureka
 * - Path predicates for request matching
 * - Filters for request/response processing
 * - Circuit breaker configuration for fault tolerance
 * 
 * Route Pattern: /api/{service-name}/** -> lb://{service-name}/api/{service-name}/**
 */
@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private LoggingFilter loggingFilter;

    /**
     * Configure Gateway Routes
     * 
     * Defines routes to all microservices with:
     * - Load balancing (lb://)
     * - Request logging
     * - Circuit breaker protection
     * - NO path rewriting (services expect /api prefix)
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            
// ========================================
// USER SERVICE ROUTES - PUBLIC (No Authentication)
// ========================================
.route("user-service-public", r -> r
    .path("/api/users/register", "/api/users/login")
    .filters(f -> f
        .filter(loggingFilter)
        // NO authenticationFilter for public endpoints
        .circuitBreaker(config -> config
            .setName("userServiceCircuitBreaker")
            .setFallbackUri("forward:/fallback/user-service"))
        .retry(retryConfig -> retryConfig
            .setRetries(3)
            .setStatuses(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)))
    .uri("lb://user-service"))

// ========================================
// USER SERVICE ROUTES - PROTECTED (With Authentication)
// ========================================
.route("user-service-protected", r -> r
    .path("/api/users/**")
    .filters(f -> f
        .filter(loggingFilter)
        .filter(authenticationFilter) // JWT authentication for protected routes
        .circuitBreaker(config -> config
            .setName("userServiceCircuitBreaker")
            .setFallbackUri("forward:/fallback/user-service"))
        .retry(retryConfig -> retryConfig
            .setRetries(3)
            .setStatuses(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)))
    .uri("lb://user-service"))
            
            // ========================================
            // FREELANCER SERVICE ROUTES
            // ========================================
            .route("freelancer-service", r -> r
                .path("/api/freelancers/**")
                .filters(f -> f
                    .filter(loggingFilter)
                    .filter(authenticationFilter)
                    .circuitBreaker(config -> config
                        .setName("freelancerServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/freelancer-service"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setStatuses(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)))
                .uri("lb://freelancer-service"))
            
            // ========================================
            // PROJECT SERVICE ROUTES
            // ========================================
            .route("project-service", r -> r
                .path("/api/projects/**")
                .filters(f -> f
                    .filter(loggingFilter)
                    .filter(authenticationFilter)
                    .circuitBreaker(config -> config
                        .setName("projectServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/project-service"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setStatuses(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)))
                .uri("lb://project-service"))
            
            // ========================================
            // PAYMENT SERVICE ROUTES
            // ========================================
            .route("payment-service", r -> r
                .path("/api/payments/**")
                .filters(f -> f
                    .filter(loggingFilter)
                    .filter(authenticationFilter)
                    .circuitBreaker(config -> config
                        .setName("paymentServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/payment-service"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setStatuses(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)))
                .uri("lb://payment-service"))
            
            // ========================================
            // NOTIFICATION SERVICE ROUTES
            // ========================================
            .route("notification-service", r -> r
                .path("/api/notifications/**")
                .filters(f -> f
                    .filter(loggingFilter)
                    .filter(authenticationFilter)
                    .circuitBreaker(config -> config
                        .setName("notificationServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/notification-service"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setStatuses(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)))
                .uri("lb://notification-service"))
            
            .build();
    }
}