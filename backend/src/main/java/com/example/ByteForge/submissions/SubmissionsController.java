package com.example.ByteForge.submissions;

import com.example.ByteForge.judge0.Judge0Service;
import com.example.ByteForge.judge0.dto.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.Judge0ResponseDto;
import com.example.ByteForge.problems.ProblemsService;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.exceptions.ProblemNotFoundException;
import com.example.ByteForge.submissions.dto.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.dto.SubmissionDto;
import com.example.ByteForge.submissions.entities.SubmissionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user/submissions")
public class SubmissionsController {

    @Autowired
    private Judge0Service judge0ExecutionService;

    @Autowired
    private ProblemsService problemsService;

    @GetMapping("/{problem_id}/submissions")
    List<SubmissionDto> findSubmissionsByProblemAndUserId(@PathVariable Long id) {
        throw new RuntimeException("Unimplemented");
    }

    @PostMapping("/submit")
    SubmitCodeResponseDto submitCode(@RequestBody SubmitCodeRequestDto requestDto) {
        Long problemId = requestDto.getProblemId();
        int languageId = requestDto.getLanguageId();
        String sourceCode = requestDto.getSourceCode();

        Optional<ProblemEntity> problemResponse = problemsService.findProblemById(problemId);
        if (problemResponse.isEmpty()) throw new ProblemNotFoundException("Invalid problem id");

        ProblemEntity problem = problemResponse.get();
        int boilerPlateCodeIdx = -1;

        for (int i = 0; i < problem.getBoilerPlateCodes().size(); i++) {
            if (problem.getBoilerPlateCodes().get(i).getLanguageCode() == languageId) {
                boilerPlateCodeIdx = i;
                break;
            }
        }
        if (boilerPlateCodeIdx == -1) throw new ProblemNotFoundException("Invalid language code");
        sourceCode = problem.getBoilerPlateCodes().get(boilerPlateCodeIdx).getPrependCode() + sourceCode + problem.getBoilerPlateCodes().get(boilerPlateCodeIdx).getAppendCode();

        int totalTestCases = problem.getTestCases().size();
        int passed = 0;

        for (var testCase: problem.getTestCases()) {
            Judge0RequestDto requestPayload = new Judge0RequestDto(
                    sourceCode,
                    languageId,
                    testCase.getInput(),
                    problem.getTimeLimitInMS() / 1000.0, // in seconds
                    (int)(problem.getMemoryLimitInMB() * 1024), // in KB
                    5.0 // max time for cpu usage in seconds
            );

            Judge0ResponseDto response = judge0ExecutionService.executeCode(requestPayload);
            if (Objects.requireNonNull(response.getMappedStatus()) == SubmissionStatus.ACC) {
                if (response.stdout().equals(testCase.getOutput())) {
                    passed++;
                } else {
                    SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
                    dto.setStatus(SubmissionStatus.WA);
                    dto.setCodeOutput(response.stdout());

                    dto.setExpectedOutput("Hidden");
                    if (!testCase.getHidden()) dto.setExpectedOutput(testCase.getOutput());

                    dto.setInput(testCase.getInput());
                    dto.setTotalPassed(passed);
                    dto.setTotalTestCases(totalTestCases);
                    dto.setHiddenTestCase(testCase.getHidden());
                    dto.setError("Wrong Answer");

                    return dto;
                }
            } else {
                SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
                dto.setError(response.compileOutput());
                dto.setStatus(SubmissionStatus.CE);

                return dto;
            }
        }

        SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
        dto.setTotalTestCases(totalTestCases);
        dto.setTotalPassed(passed);
        dto.setStatus(SubmissionStatus.ACC);

        return dto;

    }
}
