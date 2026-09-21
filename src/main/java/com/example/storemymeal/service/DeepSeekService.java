package com.example.storemymeal.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;
    @Value("${app.mock-ai:false}") // Reads the flag from application.yaml
    private boolean mockAi;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode analyzeFood(String foodDescription) {
        if (mockAi) {
            System.out.println("-> [MOCK MODE] Bypassing DeepSeek API call for: " + foodDescription);
            String mockJson = "{"
                    + "\"calories\": 400.0,"
                    + "\"protein\": 25.0,"
                    + "\"fat\": 10.0,"
                    + "\"fiber\": 6.0,"
                    + "\"suggestion\": \"Mock AI Suggestion: This is a balanced meal, great for testing your app locally!\""
                    + "}";
            try {
                return objectMapper.readTree(mockJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse mock JSON", e);
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = "You are a nutritional assistant. Return ONLY a valid raw JSON object without markdown fences: "
                + "{\"calories\": number, \"protein\": number, \"fat\": number, \"fiber\": number, \"suggestion\": string}";

        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", "Analyze this meal: " + foodDescription)
                ),
                "response_format", Map.of("type", "json_object")
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String rawJsonContent = root.path("choices").get(0).path("message").path("content").asText();
            return objectMapper.readTree(rawJsonContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DeepSeek JSON response", e);
        }
    }
}