package com.freelancenexus.userservice.service;

import com.freelancenexus.userservice.dto.LoginResponseDTO;
import com.freelancenexus.userservice.dto.UserLoginDTO;
import com.freelancenexus.userservice.dto.UserRegistrationDTO;
import com.freelancenexus.userservice.exception.AuthenticationException;
import com.freelancenexus.userservice.exception.KeycloakException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.ws.rs.core.Response;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {
    
	private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);
	
    private final Keycloak keycloak;
    private final RestTemplate restTemplate;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    public String createKeycloakUser(UserRegistrationDTO registrationDTO) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            // Create user representation
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEmail(registrationDTO.getEmail());
            userRepresentation.setUsername(registrationDTO.getEmail());
            userRepresentation.setFirstName(registrationDTO.getFullName().split(" ")[0]);
            userRepresentation.setLastName(registrationDTO.getFullName().contains(" ") 
                ? registrationDTO.getFullName().substring(registrationDTO.getFullName().indexOf(" ") + 1) 
                : "");
            userRepresentation.setEnabled(true);
            userRepresentation.setEmailVerified(true);
            
            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(registrationDTO.getPassword());
            credential.setTemporary(false);
            userRepresentation.setCredentials(Collections.singletonList(credential));
            
            // Create user in Keycloak
            Response response = usersResource.create(userRepresentation);
            
            if (response.getStatus() != 201) {
                log.error("Failed to create user in Keycloak. Status: {}", response.getStatus());
                throw new KeycloakException("Failed to create user in Keycloak");
            }
            
            // Extract user ID from location header
            String locationHeader = response.getHeaderString("Location");
            String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
            
            // Assign role to user
            assignRoleToUser(userId, registrationDTO.getRole().name());
            
            log.info("User created successfully in Keycloak with ID: {}", userId);
            return userId;
            
        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", e.getMessage(), e);
            throw new KeycloakException("Error creating user in Keycloak: " + e.getMessage());
        }
    }
    
    public void assignRoleToUser(String userId, String roleName) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            
            // Get realm role
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            
            // Assign role to user
            realmResource.users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(role));
            
            log.info("Role {} assigned to user {}", roleName, userId);
            
        } catch (Exception e) {
            log.error("Error assigning role to user: {}", e.getMessage(), e);
            throw new KeycloakException("Error assigning role: " + e.getMessage());
        }
    }
    
    public LoginResponseDTO authenticateUser(UserLoginDTO loginDTO) {
        try {
            String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "password");
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("username", loginDTO.getEmail());
            requestBody.add("password", loginDTO.getPassword());
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> tokenResponse = response.getBody();
                
                LoginResponseDTO loginResponse = new LoginResponseDTO();
                loginResponse.setAccessToken((String) tokenResponse.get("access_token"));
                loginResponse.setRefreshToken((String) tokenResponse.get("refresh_token"));
                loginResponse.setExpiresIn(((Number) tokenResponse.get("expires_in")).longValue());
                loginResponse.setTokenType("Bearer");
                
                return loginResponse;
            } else {
                throw new AuthenticationException("Invalid credentials");
            }
            
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
    
    public void deleteKeycloakUser(String keycloakId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            Response response = usersResource.delete(keycloakId);
            
            if (response.getStatus() != 204) {
                log.error("Failed to delete user from Keycloak. Status: {}", response.getStatus());
                throw new KeycloakException("Failed to delete user from Keycloak");
            }
            
            log.info("User deleted successfully from Keycloak: {}", keycloakId);
            
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", e.getMessage(), e);
            throw new KeycloakException("Error deleting user from Keycloak: " + e.getMessage());
        }
    }
    
    public String getKeycloakUserIdByEmail(String email) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            List<UserRepresentation> users = realmResource.users().search(email, true);
            
            if (users.isEmpty()) {
                return null;
            }
            
            return users.get(0).getId();
            
        } catch (Exception e) {
            log.error("Error getting Keycloak user by email: {}", e.getMessage(), e);
            return null;
        }
    }
}