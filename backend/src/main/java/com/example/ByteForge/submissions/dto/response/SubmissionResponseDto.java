package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.problems.dto.response.TestCaseResponseDto;
import com.example.ByteForge.submissions.entities.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponseDto {
    private Long id;
    private int languageId;
    private String submissionCode;
    private TestCaseResponseDto failedOnTestCase;
    private SubmissionStatus submissionStatus;
    private String codeOutput;
    private String userLogs;
    private LocalDateTime submissionDateTime;
}