package com.freelancenexus.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * Security Configuration for API Gateway
 * 
 * This configuration handles:
 * - JWT token validation from Keycloak
 * - Public and protected endpoint definitions
 * - CORS policy enforcement
 * - OAuth2 resource server setup
 * 
 * Public Endpoints (no authentication required):
 * - User registration and login
 * - Public freelancer profiles
 * - Public project listings
 * - Health check endpoints
 * 
 * Protected Endpoints (authentication required):
 * - All other API endpoints
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * Configure Security Filter Chain
     * 
     * Defines which endpoints require authentication and which are public.
     * Uses OAuth2 JWT for token validation.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // Disable form login
            .formLogin(formLogin -> formLogin.disable())
            
            // Disable HTTP Basic authentication
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // Use stateless security context (no session)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            
            // Configure authorization rules
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints - no authentication required
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/api/users/register").permitAll()
                .pathMatchers("/api/users/login").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/freelancers/public/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/projects/public/**").permitAll()
                
                // All other endpoints require authentication
                .anyExchange().authenticated()
            )
            
            // Configure OAuth2 Resource Server with JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );

        return http.build();
    }

    /**
     * JWT Decoder Bean
     * 
     * Configures the JWT decoder to validate tokens against Keycloak's JWK Set.
     * The JWK Set contains public keys used to verify JWT signatures.
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}