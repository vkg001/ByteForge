package com.example.ByteForge.judge0.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Judge0RequestDto(
        @JsonProperty("source_code") String sourceCode,
        @JsonProperty("language_id") int languageId,
        String stdin,
        @JsonProperty("cpu_time_limit") double cpuTimeLimit,
        @JsonProperty("memory_limit") int memoryLimit,
        @JsonProperty("wall_time_limit") double wallTimeLimit
) {}
