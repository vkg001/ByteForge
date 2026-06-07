package com.example.ByteForge.submissions;

import com.example.ByteForge.config.AppConfig;
import com.example.ByteForge.judge0.Judge0Service;
import com.example.ByteForge.judge0.dto.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.Judge0ResponseDto;
import com.example.ByteForge.problems.ProblemsService;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.entities.TestCaseEntity;
import com.example.ByteForge.problems.exceptions.ProblemNotFoundException;
import com.example.ByteForge.submissions.dto.request.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.response.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.entities.ProblemSubmissionStatus;
import com.example.ByteForge.submissions.dto.response.SubmissionsListResponseDto;
import com.example.ByteForge.submissions.entities.SubmissionEntity;
import com.example.ByteForge.submissions.entities.SubmissionStatus;
import com.example.ByteForge.user.UsersService;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
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

    @Autowired
    private UsersService usersService;

    public SubmissionsListResponseDto findSubmissionByProblemAndUserId(Long problemId, Long userId, Pageable pageable) {
        List<SubmissionEntity> submissions = repository.findSubmissionByProblemAndUserId(problemId, userId, pageable);
        SubmissionsListResponseDto res = new SubmissionsListResponseDto();
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

    @Transactional
    public SubmitCodeResponseDto processSubmission(SubmitCodeRequestDto requestDto) {
        Long problemId = requestDto.getProblemId();
        int languageId = requestDto.getLanguageId();
        String sourceCode = requestDto.getSourceCode();

        ProblemEntity problem = problemsService.findProblemByIdGetEntity(problemId)
                .orElseThrow(() -> new ProblemNotFoundException("Invalid problem id"));

        log.warn("Problem fet {}", problem);

        String finalSourceCode = problem.getBoilerPlateCodes().stream()
                .filter(bp -> bp.getLanguageCode() == languageId)
                .findFirst()
                .map(bp -> bp.getPrependCode() + sourceCode + bp.getAppendCode())
                .orElseThrow(() -> new ProblemNotFoundException("Invalid language code"));
//        log.warn("Final Source Code: {}", finalSourceCode);
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
                saveSubmission(problem, languageId, finalSourceCode, testCase, response);
                return dto;
            }

            if (response.getMappedStatus() == SubmissionStatus.RTE) {
                SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
                dto.setError(response.stderr());
                dto.setStatus(SubmissionStatus.RTE);
                saveSubmission(problem, languageId, finalSourceCode, testCase, response);
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

                saveSubmission(problem,languageId, finalSourceCode, testCase, response);
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

        saveSubmission(problem, languageId, finalSourceCode, null, null);
        return dto;
    }

    public void saveSubmission(ProblemEntity problemEntity, int languageId, String submissionCode, TestCaseEntity failedTestCase, @Nullable Judge0ResponseDto executionResponse) {
        SubmissionEntity submissionEntity = new SubmissionEntity();

        submissionEntity.setProblem(problemEntity);
        submissionEntity.setLanguageId(languageId);
        submissionEntity.setUser(usersService.getCurrentUserDetailsInEntity());
        submissionEntity.setSubmissionCode(submissionCode);
        submissionEntity.setFailedOnTestCase(failedTestCase);
        submissionEntity.setSubmissionStatus(executionResponse == null ? SubmissionStatus.ACC : executionResponse.getMappedStatus());
        submissionEntity.setCodeOutput(executionResponse != null  &&  executionResponse.stdout() != null ? executionResponse.stdout().trim() : "");
        submissionEntity.setUserLogs(executionResponse != null ? executionResponse.stderr() : "");

        repository.save(submissionEntity);
    }
}
