package com.example.ByteForge.submissions.service;

import com.example.ByteForge.config.AppConfig;
import com.example.ByteForge.judge0.Judge0Service;
import com.example.ByteForge.judge0.dto.request.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.response.Judge0ResponseDto;
import com.example.ByteForge.problems.core.services.ProblemService;
import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.problems.core.entities.TestCaseEntity;
import com.example.ByteForge.problems.core.exceptions.ProblemNotFoundException;
import com.example.ByteForge.problems.stats.service.ProblemStatsService;
import com.example.ByteForge.submissions.events.SubmissionEvent;
import com.example.ByteForge.submissions.repository.SubmissionRepository;
import com.example.ByteForge.submissions.dto.request.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.dto.response.SubmitCodeResponseDto;
import com.example.ByteForge.submissions.enums.ProblemSubmissionStatus;
import com.example.ByteForge.submissions.dto.response.SubmissionListResponseDto;
import com.example.ByteForge.submissions.entity.SubmissionEntity;
import com.example.ByteForge.submissions.enums.SubmissionStatus;
import com.example.ByteForge.submissions.mapper.SubmissionMapper;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.core.service.UsersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository repository;
    private final Judge0Service judge0Service;
    private final ProblemService problemService;
    private final AppConfig appConfig;
    private final UsersService usersService;
    private final SubmissionMapper submissionMapper;
    private final ProblemStatsService problemStatsService;
    private final ApplicationEventPublisher eventPublisher;

    public SubmissionListResponseDto findSubmissionByProblemAndUserId(Long problemId, Long userId, Pageable pageable) {
        List<SubmissionEntity> submissions = repository.findSubmissionByProblemAndUserId(problemId, userId, pageable);
        SubmissionListResponseDto res = new SubmissionListResponseDto();
        res.setAllSubmissions(submissionMapper.toResponseDtoList(submissions));

        if (submissions.isEmpty()) {
            res.setStatus(ProblemSubmissionStatus.UAT);
            return res;
        }

        boolean hasAccepted = submissions.stream()
                .anyMatch(sub -> sub.getSubmissionStatus() == SubmissionStatus.ACC);

        res.setStatus(hasAccepted ? ProblemSubmissionStatus.ACC : ProblemSubmissionStatus.ATT);
        return res;
    }

    @Transactional
    public SubmitCodeResponseDto processSubmission(SubmitCodeRequestDto requestDto) {
        ProblemEntity problem = problemService.findProblemByIdGetEntity(requestDto.getProblemId())
                .orElseThrow(() -> new ProblemNotFoundException("Invalid problem id"));

        String finalSourceCode = buildFinalSourceCode(problem, requestDto.getLanguageId(), requestDto.getSourceCode());
        String combinedInput = buildCombinedInput(problem.getTestCases());

        Judge0RequestDto batchRequest = new Judge0RequestDto(
                finalSourceCode,
                requestDto.getLanguageId(),
                combinedInput,
                null,
                (int)(problem.getTimeLimitInMS() / 1000),
                (int) (problem.getMemoryLimitInMB() * 1024),
                20.0
        );

        List<String> tokens = judge0Service.submitBatch(List.of(batchRequest));
        Judge0ResponseDto response = judge0Service.getBatchResults(tokens).get(0);

        EvaluationResult evaluation = evaluateSingleRun(problem, response);

        saveSubmission(problem, requestDto.getLanguageId(), requestDto.getSourceCode(), evaluation.failedTestCase(), evaluation.failedResponse());

        return buildResponseDto(evaluation, problem.getTestCases().size());
    }

    private String buildFinalSourceCode(ProblemEntity problem, int languageId, String sourceCode) {
        return problem.getBoilerPlateCodes().stream()
                .filter(bp -> bp.getLanguageCode() == languageId)
                .findFirst()
                .map(bp -> bp.getPrependCode() + "\n" + sourceCode + "\n" + bp.getAppendCode())
                .orElseThrow(() -> new ProblemNotFoundException("Invalid language code"));
    }

    private String buildCombinedInput(List<TestCaseEntity> testCases) {
        StringBuilder sb = new StringBuilder();
        sb.append(testCases.size()).append("\n");
        for (TestCaseEntity tc : testCases) {
            sb.append(tc.getInput().trim()).append("\n");
        }
        return sb.toString();
    }

    private EvaluationResult evaluateSingleRun(ProblemEntity problem, Judge0ResponseDto response) {
        SubmissionStatus globalStatus = response.getMappedStatus();
        List<TestCaseEntity> testCases = problem.getTestCases();

        if (globalStatus == SubmissionStatus.CE) {
            return new EvaluationResult(globalStatus, 0, testCases.get(0), response);
        }

        String rawOutput = response.stdout() != null ? response.stdout() : "";
        int passed = 0;

        for (TestCaseEntity testCase : testCases) {
            String beginTag = "~CASE_BEGIN~";
            String endTag = "~CASE_END~";

            int beginIdx = rawOutput.indexOf(beginTag);
            int endIdx = rawOutput.indexOf(endTag);

            if (beginIdx == -1 || endIdx == -1 || beginIdx > endIdx) {
                if (globalStatus == SubmissionStatus.TLE) {
                    return new EvaluationResult(SubmissionStatus.TLE, passed, testCase, response);
                }
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response);
            }

            String caseBlock = rawOutput.substring(beginIdx + beginTag.length(), endIdx).trim();
            rawOutput = rawOutput.substring(endIdx + endTag.length());

            int timeTagStart = caseBlock.indexOf("~TIME|");
            int timeTagEnd = caseBlock.indexOf("~", timeTagStart + 6);

            if (timeTagStart == -1 || timeTagEnd == -1) {
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response);
            }

            try {
                double executionTimeMs = Double.parseDouble(caseBlock.substring(timeTagStart + 6, timeTagEnd).trim());
                if (executionTimeMs > problem.getTimeLimitInMS()) {
                    return new EvaluationResult(SubmissionStatus.TLE, passed, testCase, response);
                }
            } catch (NumberFormatException e) {
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response);
            }

            String actualOutput = caseBlock.substring(0, timeTagStart).trim();
            String expectedOutput = testCase.getOutput() != null ? testCase.getOutput().trim() : "";

            if (!actualOutput.equals(expectedOutput)) {
                return new EvaluationResult(SubmissionStatus.WA, passed, testCase, response);
            }

            passed++;
        }

        return new EvaluationResult(SubmissionStatus.ACC, passed, null, response);
    }

    private SubmitCodeResponseDto buildResponseDto(EvaluationResult eval, int totalTestCases) {
        SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
        dto.setStatus(eval.status());
        dto.setTotalPassed(eval.passedCount());
        dto.setTotalTestCases(totalTestCases);

        Judge0ResponseDto response = eval.failedResponse();
        if (response != null) {
            if (response.stdout() != null) {
                String cleanOutput = response.stdout()
                        .replaceAll("~CASE_BEGIN~\\s*", "")
                        .replaceAll("~CASE_END~\\s*", "")
                        .replaceAll("~TIME\\|[0-9.]+\\s*~\\s*", "")
                        .trim();
                dto.setCodeOutput(cleanOutput);
            } else {
                dto.setCodeOutput("");
            }

            dto.setUserLogs(response.stderr() != null ? response.stderr().trim() : "");

            if (eval.status() == SubmissionStatus.CE) {
                dto.setError(response.compileOutput());
                return dto;
            }

            if (eval.status() == SubmissionStatus.RTE) {
                String fallbackError = (response.stderr() != null && !response.stderr().trim().isEmpty())
                        ? response.stderr().trim()
                        : "Runtime Error / Process Crashed";
                dto.setError(fallbackError);
            } else if (eval.status() == SubmissionStatus.WA) {
                dto.setError("Wrong Answer");
            } else if (eval.status() == SubmissionStatus.TLE) {
                dto.setError("Time Limit Exceeded");
            }
        }

        TestCaseEntity testCase = eval.failedTestCase();
        if (testCase != null) {
            dto.setInput(testCase.getInput());
            dto.setHiddenTestCase(testCase.getHidden());

            dto.setExpectedOutput("Hidden");
            if (appConfig.isDevProfile() || !testCase.getHidden()) {
                dto.setExpectedOutput(testCase.getOutput() != null ? testCase.getOutput().trim() : "");
            }
        }
        return dto;
    }

    @Transactional
    private void saveSubmission(ProblemEntity problemEntity, int languageId, String submissionCode,
                                TestCaseEntity failedTestCase, Judge0ResponseDto executionResponse) {
        SubmissionEntity submissionEntity = new SubmissionEntity();
        submissionEntity.setProblem(problemEntity);
        submissionEntity.setLanguageId(languageId);
        UserEntity userEntity = usersService.getCurrentUserDetailsInEntity();
        submissionEntity.setUser(userEntity);
        submissionEntity.setSubmissionCode(submissionCode);
        submissionEntity.setFailedOnTestCase(failedTestCase);

        submissionEntity.setSubmissionStatus(executionResponse == null ? SubmissionStatus.ACC : executionResponse.getMappedStatus());
        submissionEntity.setCodeOutput(executionResponse != null && executionResponse.stdout() != null ? executionResponse.stdout().trim() : "");
        submissionEntity.setUserLogs(executionResponse != null ? executionResponse.stderr() : "");

        Boolean accepted = executionResponse != null && executionResponse.getMappedStatus() == SubmissionStatus.ACC;
        SubmissionEvent event = new SubmissionEvent(problemEntity.getId(), accepted);
        eventPublisher.publishEvent(event);
        repository.save(submissionEntity);
    }

    private record EvaluationResult(SubmissionStatus status, int passedCount, TestCaseEntity failedTestCase, Judge0ResponseDto failedResponse) {}
}