package com.example.ByteForge.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping("/")
    ResponseEntity<HealthDto> health() {
        return ResponseEntity.ok(new HealthDto("ok"));
    }

    @PostMapping("/testing")
    String test(@RequestBody Map<String, Object> body) {
        log.warn("Hit success");

        // Inject or instantiate an ObjectMapper
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // Serialize the Map back to a JSON string and print it
        try {
            String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            System.out.println("Actual JSON received and parsed:\n" + jsonOutput);
        } catch (Exception e) {
            log.warn("Exception while receiving: {}", e.getMessage());
        }

        return "Success";
    }
}
