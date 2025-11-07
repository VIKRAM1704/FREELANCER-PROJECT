package com.freelancenexus.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT Utility Class
 * 
 * Provides utility methods for JWT token operations:
 * - Token validation
 * - Claims extraction
 * - Token expiration checking
 * - User information extraction
 * 
 * Works with Keycloak JWT tokens.
 */
@Slf4j
@Component
public class JwtUtil {
     
	private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
	
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    // Secret key for JWT validation (if using symmetric key)
    // For Keycloak, this is typically handled by JWK Set
    private static final String SECRET_KEY = "freelance-nexus-secret-key-minimum-256-bits-required-for-hs256";

    /**
     * Extract all claims from JWT token
     */
    public Claims extractAllClaims(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, claims -> {
            // Keycloak uses 'preferred_username' claim
            return claims.get("preferred_username", String.class);
        });
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> {
            // Keycloak uses 'sub' (subject) claim for user ID
            return claims.getSubject();
        });
    }

    /**
     * Extract email from token
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> {
            return claims.get("email", String.class);
        });
    }

    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            // Keycloak stores roles in realm_access.roles
            Map<String, Object> realmAccess = claims.get("realm_access", Map.class);
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                return (List<String>) realmAccess.get("roles");
            }
            return List.of();
        });
    }

    /**
     * Extract token expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            // Check if token is expired
            if (isTokenExpired(token)) {
                log.warn("Token is expired");
                return false;
            }

            // Extract claims to validate signature
            Claims claims = extractAllClaims(token);

            // Validate issuer
            String issuer = claims.getIssuer();
            if (issuer == null || !issuer.equals(issuerUri)) {
                log.warn("Invalid token issuer: {}", issuer);
                return false;
            }

            log.debug("Token validated successfully");
            return true;

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract client ID from token
     */
    public String extractClientId(String token) {
        return extractClaim(token, claims -> {
            return claims.get("azp", String.class); // Authorized party
        });
    }

    /**
     * Check if token has specific role
     */
    public boolean hasRole(String token, String role) {
        List<String> roles = extractRoles(token);
        return roles.contains(role);
    }

    /**
     * Check if token has any of the specified roles
     */
    public boolean hasAnyRole(String token, List<String> requiredRoles) {
        List<String> userRoles = extractRoles(token);
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }
}
