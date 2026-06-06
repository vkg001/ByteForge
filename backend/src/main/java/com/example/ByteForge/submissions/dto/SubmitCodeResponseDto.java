package com.example.ByteForge.submissions.dto;

import com.example.ByteForge.submissions.entities.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitCodeResponseDto {
    private int totalTestCases;
    private int totalPassed;
    private SubmissionStatus status;
    private Boolean hiddenTestCase;
    private String input;
    private String expectedOutput;
    private String codeOutput;
    private String error;
    private String userLogs;
}
