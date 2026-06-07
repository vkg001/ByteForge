package com.example.ByteForge.judge0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public record Judge0RequestDto(
        @JsonProperty("source_code") String sourceCode,
        @JsonProperty("language_id") int languageId,
        @JsonProperty("stdin") String stdin,
        @JsonProperty("expected_output") String expectedOutput,
        @JsonProperty("cpu_time_limit") double cpuTimeLimit,
        @JsonProperty("memory_limit") int memoryLimit,
        @JsonProperty("wall_time_limit") double wallTimeLimit
) {
    // Overriding the accessors forces Jackson to serialize the Base64 encoded version
    @Override
    public String sourceCode() {
        return encodeBase64(sourceCode);
    }

    @Override
    public String stdin() {
        return encodeBase64(stdin);
    }

    @Override
    public String expectedOutput() {
        return encodeBase64(expectedOutput);
    }

    private String encodeBase64(String input) {
        if (input == null) return null;
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}