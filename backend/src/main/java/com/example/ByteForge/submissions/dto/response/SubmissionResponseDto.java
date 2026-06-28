package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.problems.core.dto.response.TestCaseResponseDto;
import com.example.ByteForge.submissions.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TestCaseExecutionDto> codeOutput;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userLogs;

    private LocalDateTime submissionDateTime;
}