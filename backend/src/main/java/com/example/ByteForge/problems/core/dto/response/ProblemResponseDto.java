package com.example.ByteForge.problems.core.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.example.ByteForge.problems.core.enums.ProblemDifficulty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemResponseDto {
    private Long id;
    private String problemTitle;
    private String problemStatement;
    private List<String> constraints = new ArrayList<>();
    private ProblemDifficulty problemDifficulty;
    private List<String> hints = new ArrayList<>();
    private List<String> companyTags;
    private List<ExampleResponseDto> examples = new ArrayList<>();
    private List<String> topics = new ArrayList<>();
    private List<TestCaseResponseDto> testCases = new ArrayList<>();
    private Long memoryLimitInMB;
    private Long timeLimitInMS;
    private List<Long> similarQuestions = new ArrayList<>();
    private List<BoilerplateCodeResponseDto> boilerPlateCodes = new ArrayList<>();
}