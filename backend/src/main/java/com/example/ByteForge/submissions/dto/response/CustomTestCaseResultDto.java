package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.submissions.enums.SubmissionStatus;
import lombok.Data;

@Data
public class CustomTestCaseResultDto {
    private String input;
    private SubmissionStatus status;
    private String functionOutput;
    private String userOutput;
    private Double executionTimeMs;
}