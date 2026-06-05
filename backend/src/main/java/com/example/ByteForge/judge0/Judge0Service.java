package com.example.ByteForge.judge0;

import com.example.ByteForge.judge0.dto.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.Judge0ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Judge0Service {
    @Value("${judge0.api.url}")
    private String judge0BaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Judge0ResponseDto executeCode(Judge0RequestDto payload) {
        // Define the exact endpoint (Warning: wait=true is used for testing only)
        String url = judge0BaseUrl + "/submissions?wait=true";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Judge0RequestDto> requestEntity = new HttpEntity<>(payload, headers);

        try {
            return restTemplate.postForObject(url, requestEntity, Judge0ResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with execution engine: " + e.getMessage());
        }
    }
}
