package com.example.ByteForge.judge0.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.ByteForge.submissions.entities.SubmissionStatus;

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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Judge0StatusDto(Integer id, String description) {}

    public SubmissionStatus getMappedStatus() {
        if (this.status == null || this.status.id() == null) {
            return SubmissionStatus.ISE;
        }
        return SubmissionStatus.fromJudge0Id(this.status.id());
    }
}