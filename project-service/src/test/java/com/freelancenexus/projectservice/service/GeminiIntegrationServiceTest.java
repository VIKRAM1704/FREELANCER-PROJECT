package com.freelancenexus.projectservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.projectservice.config.GeminiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GeminiIntegrationServiceTest {

    @Mock private WebClient webClient;
    @Mock private GeminiConfig geminiConfig;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private GeminiIntegrationService geminiService;

    @Test
    void shouldReturnEmptyJsonNode_whenException() {
        JsonNode node = geminiService.callGeminiForJson("prompt");
        assertThat(node).isNotNull();
    }
}
