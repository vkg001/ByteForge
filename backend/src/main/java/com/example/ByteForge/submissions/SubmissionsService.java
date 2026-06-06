package com.example.ByteForge.submissions;

import com.example.ByteForge.config.AppConfig;
import com.example.ByteForge.judge0.Judge0Service;
import com.example.ByteForge.judge0.dto.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.Judge0ResponseDto;
import com.example.ByteForge.problems.ProblemsService;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.exceptions.ProblemNotFoundException;
import com.example.ByteForge.submissions.dto.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.entities.ProblemSubmissionStatus;
import com.example.ByteForge.submissions.dto.SubmissionDto;
import com.example.ByteForge.submissions.entities.SubmissionEntity;
import com.example.ByteForge.submissions.entities.SubmissionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubmissionsService {
    @Autowired
    SubmissionsRepository repository;

    @Autowired
    private Judge0Service judge0Service;

    @Autowired
    private ProblemsService problemsService;

    @Autowired
    private AppConfig appConfig;

    public SubmissionDto findSubmissionByProblemAndUserId(Long problemId, Long userId, Pageable pageable) {
        List<SubmissionEntity> submissions = repository.findSubmissionByProblemAndUserId(problemId, userId, pageable);
        SubmissionDto res = new SubmissionDto();
        res.setAllSubmissions(submissions);

        if (submissions.isEmpty()) {
            res.setStatus(ProblemSubmissionStatus.UAT);
            return res;
        }

        for (var submission: submissions) {
            if (submission.getSubmissionStatus() == SubmissionStatus.ACC) {
                res.setStatus(ProblemSubmissionStatus.ACC);
                return res;
            }
        }

        res.setStatus(ProblemSubmissionStatus.ATT);
        return res;
    }

    public SubmitCodeResponseDto processSubmission(SubmitCodeRequestDto requestDto) {
        Long problemId = requestDto.getProblemId();
        int languageId = requestDto.getLanguageId();
        String sourceCode = requestDto.getSourceCode();

        ProblemEntity problem = problemsService.findProblemById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException("Invalid problem id"));

        String finalSourceCode = problem.getBoilerPlateCodes().stream()
                .filter(bp -> bp.getLanguageCode() == languageId)
                .findFirst()
                .map(bp -> bp.getPrependCode() + sourceCode + bp.getAppendCode())
                .orElseThrow(() -> new ProblemNotFoundException("Invalid language code"));
        log.warn("Final Source Code: {}", finalSourceCode);
        int totalTestCases = problem.getTestCases().size();

        // 1. Map all test cases into a single batch request
        List<Judge0RequestDto> batchRequests = problem.getTestCases().stream().map(testCase ->
                new Judge0RequestDto(
                        finalSourceCode,
                        languageId,
                        testCase.getInput(),
                        testCase.getOutput(),
                        problem.getTimeLimitInMS() / 1000.0,
                        (int) (problem.getMemoryLimitInMB() * 1024),
                        5.0
                )
        ).collect(Collectors.toList());

        // 2. Multi-execution via Batch API
        List<String> tokens = judge0Service.submitBatch(batchRequests);
        List<Judge0ResponseDto> results = judge0Service.getBatchResults(tokens);

        // 3. Evaluate results
        int passed = 0;
        for (int i = 0; i < results.size(); i++) {
            Judge0ResponseDto response = results.get(i);
            var testCase = problem.getTestCases().get(i);

            if (response.getMappedStatus() == SubmissionStatus.CE) {
                SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
                dto.setError(response.compileOutput());
                dto.setStatus(SubmissionStatus.CE);
                return dto;
            }

            if (response.getMappedStatus() == SubmissionStatus.RTE) {
                SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
                dto.setError(response.stderr());
                dto.setStatus(SubmissionStatus.RTE);
                return dto;
            }

            if (response.getMappedStatus() == SubmissionStatus.ACC) {
                passed++;
            } else {
                SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
                dto.setStatus(response.getMappedStatus());
                dto.setCodeOutput(response.stdout() != null ? response.stdout().trim() : "");

                dto.setExpectedOutput("Hidden");
                if (appConfig.isDevProfile() || !testCase.getHidden()) {
                    dto.setExpectedOutput(testCase.getOutput() != null ? testCase.getOutput().trim() : "");
                }

                dto.setInput(testCase.getInput());
                dto.setTotalPassed(passed);
                dto.setTotalTestCases(totalTestCases);
                dto.setHiddenTestCase(testCase.getHidden());
                dto.setUserLogs(response.stderr()); // Segregated user print statement logs

                if (response.getMappedStatus() == SubmissionStatus.WA) {
                    dto.setError("Wrong Answer");
                } else if (response.getMappedStatus() == SubmissionStatus.TLE) {
                    dto.setError("Time Limit Exceeded");
                }
                return dto;
            }
        }

        SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
        dto.setTotalTestCases(totalTestCases);
        dto.setTotalPassed(passed);
        dto.setStatus(SubmissionStatus.ACC);

        if (!results.isEmpty()) {
            dto.setUserLogs(results.get(results.size() - 1).stderr());
        }
        return dto;
    }
}
