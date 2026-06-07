package com.example.ByteForge.problems.core.dto.request;

import com.example.ByteForge.problems.core.enums.ProblemDifficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemRequestDto {
    private String problemTitle;
    private String problemStatement;
    private List<String> constraints = new ArrayList<>();
    private ProblemDifficulty problemDifficulty;
    private List<String> hints = new ArrayList<>();
    private List<String> companyTags;
    private List<ExampleRequestDto> examples = new ArrayList<>();
    private List<String> topics = new ArrayList<>();
    private List<TestCaseRequestDto> testCases = new ArrayList<>();
    private Long memoryLimitInMB;
    private Long timeLimitInMS;
    private List<Long> similarQuestions = new ArrayList<>();
    private List<BoilerplateCodeRequestDto> boilerPlateCodes = new ArrayList<>();
}