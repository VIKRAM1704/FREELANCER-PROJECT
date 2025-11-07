package com.freelancenexus.gateway.filter;

import com.freelancenexus.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class AuthenticationFilter implements GatewayFilter {
	
	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	@Autowired
	private JwtUtil jwtUtil;
    // List of public endpoints that don't require authentication
	private static final List<String> PUBLIC_ENDPOINTS = List.of("/api/users/register", "/api/users/login",
			"/api/freelancers/public", "/api/projects/public", "/actuator");

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		log.debug("Authentication filter invoked for path: {}", path);

		// Skip authentication for public endpoints
		if (isPublicEndpoint(path)) {
			log.debug("Public endpoint detected, skipping authentication: {}", path);
			return chain.filter(exchange);
		}

		// Extract Authorization header
		if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
			log.warn("Missing Authorization header for protected endpoint: {}", path);
			return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
		}

		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		// Validate Bearer token format
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Invalid Authorization header format: {}", authHeader);
			return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
		}

		// Extract JWT token
		String token = authHeader.substring(7);

		try {
			// Validate JWT token
			if (!jwtUtil.validateToken(token)) {
				log.warn("Invalid or expired JWT token");
				return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
			}

			// Extract user information from token
			String userId = jwtUtil.extractUserId(token);
			String username = jwtUtil.extractUsername(token);
			String email = jwtUtil.extractEmail(token);
			List<String> roles = jwtUtil.extractRoles(token);

			log.debug("JWT token validated successfully for user: {}", username);

			// Add user context to request headers for downstream services
			ServerHttpRequest modifiedRequest = request.mutate().header("X-User-Id", userId)
					.header("X-Username", username).header("X-User-Email", email)
					.header("X-User-Roles", String.join(",", roles)).build();

			// Continue with modified request
			return chain.filter(exchange.mutate().request(modifiedRequest).build());

		} catch (Exception e) {
			log.error("Error validating JWT token: {}", e.getMessage());
			return onError(exchange, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * 
	 * Check if the endpoint is public (no authentication required)
	 */
	private boolean isPublicEndpoint(String path) {
		return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
	}

/**

Handle authentication errors
*/
private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
ServerHttpResponse response = exchange.getResponse();
response.setStatusCode(status);
response.getHeaders().add("Content-Type", "application/json");
String errorResponse = String.format(
	    "{\"error\": \"%s\", \"message\": \"%s\", \"status\": %d}",
	    status.getReasonPhrase(),
	    message,
	    status.value()
	);
return response.writeWith(Mono.just(response.bufferFactory().wrap(errorResponse.getBytes())));
}
}
