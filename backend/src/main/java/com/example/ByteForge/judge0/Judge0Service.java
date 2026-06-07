package com.example.ByteForge.judge0;

import com.example.ByteForge.judge0.dto.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.Judge0ResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Judge0Service {

    @Value("${judge0.api.url}")
    private String judge0BaseUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> submitBatch(List<Judge0RequestDto> requests) {
        // Enforcing Base64 encoding
        String url = judge0BaseUrl + "/submissions/batch?base64_encoded=true";

        try {
            String jsonBody = objectMapper.writeValueAsString(Map.of("submissions", requests));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());

            // Failsafe: check for top-level errors before parsing tokens
            if (root.has("error")) {
                throw new RuntimeException("Judge0 error during batch submission: " + root.toString());
            }

            return root.findValuesAsText("token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit batch to Judge0: " + e.getMessage(), e);
        }
    }

    public List<Judge0ResponseDto> getBatchResults(List<String> tokens) {
        String tokenString = String.join(",", tokens);
        // Enforcing Base64 encoding
        String url = judge0BaseUrl + "/submissions/batch?tokens=" + tokenString + "&base64_encoded=true";

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            boolean allFinished = false;
            JsonNode submissions = null;

            while (!allFinished) {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode root = objectMapper.readTree(response.body());

                // Fixed: Do not proceed if submissions are missing. Throw immediately.
                if (root.get("submissions") == null) {
                    throw new RuntimeException("Judge0 API returned an invalid response. Expected 'submissions' array. Body: " + root.toString());
                }

                submissions = root.get("submissions");

                allFinished = true;
                for (JsonNode sub : submissions) {
                    if (sub.get("status") == null  || sub.get("status").get("id") == null) continue;

                    int statusId = sub.get("status").get("id").asInt();
                    if (statusId <= 2) { // 1 = In Queue, 2 = Processing
                        allFinished = false;
                        break;
                    }
                }

                if (!allFinished) {
                    Thread.sleep(1000);
                }
            }

            return objectMapper.readerForListOf(Judge0ResponseDto.class).readValue(submissions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve batch execution results: " + e.getMessage(), e);
        }
    }
}