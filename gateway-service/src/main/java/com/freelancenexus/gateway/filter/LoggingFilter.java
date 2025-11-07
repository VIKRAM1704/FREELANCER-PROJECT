package com.freelancenexus.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Logging Filter
 * 
 * This filter logs all incoming requests and outgoing responses
 * for monitoring and debugging purposes.
 * 
 * Logged Information:
 * - HTTP method and path
 * - Request headers
 * - Response status
 * - Processing time
 */
@Slf4j
@Component
public class LoggingFilter implements GatewayFilter {
	
	private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        // Log incoming request
        log.info("========================================");
        log.info("Incoming Request:");
        log.info("Method: {}", request.getMethod());
        log.info("Path: {}", request.getURI().getPath());
        log.info("Query Params: {}", request.getURI().getQuery());
        log.info("Headers: {}", request.getHeaders());
        log.info("Remote Address: {}", request.getRemoteAddress());
        log.info("========================================");

        // Continue filter chain and log response
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("========================================");
            log.info("Outgoing Response:");
            log.info("Status Code: {}", response.getStatusCode());
            log.info("Headers: {}", response.getHeaders());
            log.info("Processing Time: {} ms", duration);
            log.info("========================================");
        }));
    }
}
