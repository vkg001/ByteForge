package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.submissions.enums.SubmissionStatus;
import lombok.Data;
import java.util.List;

@Data
public class RunCodeResponseDto {
    private SubmissionStatus globalStatus;
    private String compileError;
    private List<CustomTestCaseResultDto> results;
}