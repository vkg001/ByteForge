package com.example.ByteForge.judge0.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.ByteForge.submissions.enums.SubmissionStatus;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Judge0ResponseDto(
        String stdout,
        String time,
        Integer memory,
        String stderr,
        String token,
        @JsonProperty("compile_output") String compileOutput,
        String message,
        Judge0StatusDto status
) {
    // Compact constructor decodes incoming Base64 data immediately upon deserialization
    public Judge0ResponseDto {
        stdout = decodeBase64(stdout);
        stderr = decodeBase64(stderr);
        compileOutput = decodeBase64(compileOutput);
        message = decodeBase64(message);
    }

    private static String decodeBase64(String input) {
        if (input == null || input.isBlank()) return input;
        try {
            // Strip any whitespace/newlines Judge0 might inject before decoding
            String cleanInput = input.replaceAll("\\s", "");
            return new String(Base64.getDecoder().decode(cleanInput), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // Fallback in case Judge0 returns a raw string despite the flag
            return input;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Judge0StatusDto(Integer id, String description) {}

    public SubmissionStatus getMappedStatus() {
        if (this.status == null || this.status.id() == null) {
            return SubmissionStatus.ISE;
        }
        return SubmissionStatus.fromJudge0Id(this.status.id());
    }
}