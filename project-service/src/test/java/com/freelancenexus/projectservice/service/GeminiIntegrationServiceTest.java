package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.freelancenexus.projectservice.config.GeminiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeminiIntegrationServiceTest {

    @Mock
    private WebClient geminiWebClient;

    @Mock
    private GeminiConfig geminiConfig;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GeminiIntegrationService geminiService;

    @Mock
    private RequestBodyUriSpec uriSpec;

    @Mock
    private RequestBodySpec bodySpec;

    @Mock
    private RequestHeadersSpec headersSpec;  // Removed generic type

    @Mock
    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        when(geminiConfig.getModel()).thenReturn("test-model");
        when(geminiConfig.getApiKey()).thenReturn("test-key");
        when(geminiConfig.getTimeout()).thenReturn(5000);  // Changed from 5000L to 5000
        when(geminiConfig.getMaxRetries()).thenReturn(2);
    }

    @Test
    void shouldCallGeminiSuccessfully() {
        String prompt = "Test prompt";

        when(geminiWebClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"response text\"}]}}]}"));

        String result = geminiService.callGemini(prompt);

        assertEquals("response text", result);
    }

    @Test
    void shouldReturnErrorMessageWhenGeminiReturnsError() {
        String prompt = "Test prompt";

        when(geminiWebClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"error\":\"Something went wrong\"}"));

        String result = geminiService.callGemini(prompt);

        assertTrue(result.contains("Error from API"));
    }

    @Test
    void shouldCallGeminiForJsonSuccessfully() throws Exception {
        String prompt = "Test prompt";
        String jsonResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"key\\\":\\\"value\\\"}\"}]}}]}";

        when(geminiWebClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonResponse));
        
        ObjectNode mockNode = mock(ObjectNode.class);  // Changed from JsonNode to ObjectNode
        when(objectMapper.readTree("{\"key\":\"value\"}")).thenReturn(mockNode);

        JsonNode result = geminiService.callGeminiForJson(prompt);

        assertNotNull(result);
        verify(objectMapper).readTree("{\"key\":\"value\"}");
    }

    @Test
    void shouldReturnEmptyJsonNodeWhenExceptionOccurs() throws Exception {
        String prompt = "Test prompt";
        when(geminiService.callGemini(prompt)).thenReturn("invalid json");
        when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("parse error"));
        
        ObjectNode mockObjectNode = mock(ObjectNode.class);  // Changed to ObjectNode
        when(objectMapper.createObjectNode()).thenReturn(mockObjectNode);

        JsonNode result = geminiService.callGeminiForJson(prompt);

        assertNotNull(result);
    }

    @Test
    void shouldBuildGeminiRequestCorrectly() throws Exception {
        // Using reflection to access private method
        java.lang.reflect.Method method = GeminiIntegrationService.class.getDeclaredMethod("buildGeminiRequest", String.class);
        method.setAccessible(true);

        Object request = method.invoke(geminiService, "Test prompt");
        assertNotNull(request);
    }

    @Test
    void shouldExtractTextFromResponseCorrectly() throws Exception {
        java.lang.reflect.Method method = GeminiIntegrationService.class.getDeclaredMethod("extractTextFromResponse", String.class);
        method.setAccessible(true);

        String response = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Hello World\"}]}}]}";
        String result = (String) method.invoke(geminiService, response);

        assertEquals("Hello World", result);
    }
}