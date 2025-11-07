package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.config.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiIntegrationService {

    private final WebClient geminiWebClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    /**
     * Call Gemini API with a prompt and return the response text
     */
    public String callGemini(String prompt) {
        try {
            log.info("Calling Gemini API with prompt length: {}", prompt.length());

            Map<String, Object> requestBody = buildGeminiRequest(prompt);
            String url = String.format("/%s:generateContent?key=%s", 
                    geminiConfig.getModel(), geminiConfig.getApiKey());

            String response = geminiWebClient.post()
                    .uri(url)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(geminiConfig.getTimeout()))
                    .retryWhen(Retry.backoff(geminiConfig.getMaxRetries(), Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof Exception))
                    .onErrorResume(error -> {
                        log.error("Gemini API call failed: {}", error.getMessage());
                        return Mono.just("{\"error\": \"" + error.getMessage() + "\"}");
                    })
                    .block();

            return extractTextFromResponse(response);

        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Call Gemini API with JSON response expected
     */
    public JsonNode callGeminiForJson(String prompt) {
        try {
            String response = callGemini(prompt);
            // Extract JSON from markdown code blocks if present
            String jsonStr = response;
            if (response.contains("```json")) {
                jsonStr = response.substring(response.indexOf("```json") + 7);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
            } else if (response.contains("```")) {
                jsonStr = response.substring(response.indexOf("```") + 3);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```"));
            }
            return objectMapper.readTree(jsonStr.trim());
        } catch (Exception e) {
            log.error("Error parsing JSON from Gemini response", e);
            return objectMapper.createObjectNode();
        }
    }

    /**
     * Build the request body for Gemini API
     */
    private Map<String, Object> buildGeminiRequest(String prompt) {
        Map<String, Object> request = new HashMap<>();
        
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", new Object[]{part});
        
        request.put("contents", new Object[]{content});
        
        // Configuration for better responses
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 2048);
        request.put("generationConfig", generationConfig);
        
        return request;
    }

    /**
     * Extract text content from Gemini API response
     */
    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            if (root.has("error")) {
                log.error("Gemini API error: {}", root.get("error"));
                return "Error from API: " + root.get("error").asText();
            }
            
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            
            return "No response from Gemini API";
        } catch (Exception e) {
            log.error("Error extracting text from Gemini response", e);
            return "Error parsing response: " + e.getMessage();
        }
    }
}