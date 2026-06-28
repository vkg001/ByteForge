package com.example.ByteForge.submissions.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseExecutionDto {
    private String funcOut;
    private String executionTime;
    private String userLogs;
}
