package com.example.ByteForge.judge0;

import com.example.ByteForge.judge0.dto.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.Judge0ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class Judge0Service {
    @Value("${judge0.api.url}")
    private String judge0BaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Judge0ResponseDto executeCode(Judge0RequestDto payload) {
        String url = judge0BaseUrl + "/submissions?wait=true&base64_encoded=false";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Build map manually with exact field names Judge0 expects
        Map<String, Object> body = new HashMap<>();
        body.put("source_code", payload.sourceCode());
        body.put("language_id", payload.languageId());
        body.put("stdin", payload.stdin());
        body.put("cpu_time_limit", payload.cpuTimeLimit());
        body.put("memory_limit", payload.memoryLimit());
        body.put("wall_time_limit", payload.wallTimeLimit());

//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        try {
//            log.warn("body before send: {}", new ObjectMapper().writeValueAsString(body));
////            var res = restTemplate.postForObject("http://localhost:5001/api/health/testing", requestEntity, Judge0ResponseDto.class);
//            var res = restTemplate.postForObject(url, requestEntity, Judge0ResponseDto.class);
//            log.warn("Response: {}", res);
//            return res;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to communicate with execution engine: " + e.getMessage());
//        }


        try {
            String jsonBody = new ObjectMapper().writeValueAsString(body);
            log.warn("Sending to Judge0: {}", jsonBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            log.warn("Judge0 raw response: {}", response.body());

            return new ObjectMapper().readValue(response.body(), Judge0ResponseDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with execution engine: " + e.getMessage());
        }
    }
}
