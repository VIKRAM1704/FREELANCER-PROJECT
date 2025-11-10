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
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()                       // ✅ parser() instead of parserBuilder()
                    .verifyWith(getSigningKey())        // ✅ verifyWith() instead of setSigningKey()
                    .build()
                    .parseSignedClaims(token)           // ✅ parseSignedClaims() instead of parseClaimsJws()
                    .getPayload();                      // ✅ getPayload() instead of getBody()
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            return userId != null ? userId.toString() : null;
        });
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            String role = claims.get("role", String.class);
            return role != null ? List.of(role) : List.of();
        });
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);

            if (isTokenExpired(token)) {
                log.warn("Token is expired");
                return false;
            }

            log.debug("Token validated successfully");
            return true;

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean hasRole(String token, String role) {
        List<String> roles = extractRoles(token);
        return roles.contains(role);
    }

    public boolean hasAnyRole(String token, List<String> requiredRoles) {
        List<String> userRoles = extractRoles(token);
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }
}
