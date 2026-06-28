package com.example.ByteForge.submissions.service;

import com.example.ByteForge.config.AppConfig;
import com.example.ByteForge.judge0.Judge0Service;
import com.example.ByteForge.judge0.dto.request.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.response.Judge0ResponseDto;
import com.example.ByteForge.problems.core.services.ProblemService;
import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.problems.core.entities.TestCaseEntity;
import com.example.ByteForge.problems.core.exceptions.ProblemNotFoundException;
import com.example.ByteForge.problems.solved.event.SolvedProblemEvent;
import com.example.ByteForge.submissions.dto.request.RunCodeRequestDto;
import com.example.ByteForge.submissions.dto.response.*;
import com.example.ByteForge.submissions.enums.SubmissionVisibility;
import com.example.ByteForge.submissions.events.SubmissionEvent;
import com.example.ByteForge.submissions.events.SubmissionUserEvent;
import com.example.ByteForge.submissions.repository.SubmissionRepository;
import com.example.ByteForge.submissions.dto.request.SubmitCodeRequestDto;
import com.example.ByteForge.submissions.enums.ProblemSubmissionStatus;
import com.example.ByteForge.submissions.entity.SubmissionEntity;
import com.example.ByteForge.submissions.enums.SubmissionStatus;
import com.example.ByteForge.submissions.mapper.SubmissionMapper;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.core.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
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
    private final UserService userService;
    private final SubmissionMapper submissionMapper;
    private final ApplicationEventPublisher eventPublisher;

    public SubmissionListResponseDto findSubmissionByProblemAndUserId(Long problemId, Long userId, Pageable pageable) {
        List<SubmissionEntity> submissions = userService.getCurrentUserDetails().getId() != userId ? repository.findSubmissionByProblemAndUserIdForPublic(problemId, userId, pageable) : repository.findSubmissionByProblemAndUserId(problemId, userId, pageable);
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

        SubmitCodeResponseDto submitCodeResponseDto = buildResponseDto(evaluation, problem.getTestCases().size());
        saveSubmission(problem, requestDto.getLanguageId(), requestDto.getSourceCode(), evaluation.failedTestCase(), submitCodeResponseDto);
        return submitCodeResponseDto;
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
            return new EvaluationResult(globalStatus, 0, testCases.get(0), response, "", "");
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
                    return new EvaluationResult(SubmissionStatus.TLE, passed, testCase, response, "", "");
                }
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response, "", "");
            }

            String caseBlock = rawOutput.substring(beginIdx + beginTag.length(), endIdx).trim();
            rawOutput = rawOutput.substring(endIdx + endTag.length());

            int logsTagStart = caseBlock.indexOf("~USER_LOGS~");
            int funcOutTagStart = caseBlock.indexOf("~FUNC_OUT~");
            int timeTagStart = caseBlock.indexOf("~TIME|");

            if (logsTagStart == -1 || funcOutTagStart == -1 || timeTagStart == -1) {
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response, "", "");
            }

            int timeTagEnd = caseBlock.indexOf("~", timeTagStart + 6);
            if (timeTagEnd == -1) {
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response, "", "");
            }

            String userLogs = caseBlock.substring(logsTagStart + 11, funcOutTagStart).trim();
            String actualOutput = caseBlock.substring(funcOutTagStart + 10, timeTagStart).trim();

            try {
                double executionTimeMs = Double.parseDouble(caseBlock.substring(timeTagStart + 6, timeTagEnd).trim());
                if (executionTimeMs > problem.getTimeLimitInMS()) {
                    return new EvaluationResult(SubmissionStatus.TLE, passed, testCase, response, actualOutput, userLogs);
                }
            } catch (NumberFormatException e) {
                return new EvaluationResult(SubmissionStatus.RTE, passed, testCase, response, actualOutput, userLogs);
            }

            String expectedOutput = testCase.getOutput() != null ? testCase.getOutput().trim() : "";

            if (!actualOutput.equals(expectedOutput)) {
                return new EvaluationResult(SubmissionStatus.WA, passed, testCase, response, actualOutput, userLogs);
            }

            passed++;
        }

        return new EvaluationResult(SubmissionStatus.ACC, passed, null, response, "", "");
    }

    private SubmitCodeResponseDto buildResponseDto(EvaluationResult eval, int totalTestCases) {
        SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
        dto.setStatus(eval.status());
        dto.setTotalPassed(eval.passedCount());
        dto.setTotalTestCases(totalTestCases);

        Judge0ResponseDto response = eval.failedResponse();
        if (response != null) {
            dto.setCodeOutput(eval.actualOutput());

            String stderrFallback = response.stderr() != null ? response.stderr().trim() : "";
            dto.setUserLogs(!eval.userLogs().isEmpty() ? eval.userLogs() : stderrFallback);

            if (eval.status() == SubmissionStatus.CE) {
                dto.setError(response.compileOutput());
                return dto;
            }

            if (eval.status() == SubmissionStatus.RTE) {
                String fallbackError = !stderrFallback.isEmpty() ? stderrFallback : "Runtime Error / Process Crashed";
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
                                TestCaseEntity failedTestCase, SubmitCodeResponseDto responseDto) {

        SubmissionEntity submissionEntity = new SubmissionEntity();
        submissionEntity.setProblem(problemEntity);
        submissionEntity.setLanguageId(languageId);

        SubmissionVisibility vis;
        switch (problemEntity.getProblemVisibility()) {
            case PUBLIC -> vis = SubmissionVisibility.PUBLIC;
            case PREMIUM -> vis = SubmissionVisibility.PREMIUM;
            case CONTEST -> vis = SubmissionVisibility.CONTEST;
            default -> vis = SubmissionVisibility.HIDDEN;
        }
        submissionEntity.setSubmissionVisibility(vis);

        UserEntity userEntity = userService.getCurrentUserDetailsInEntity();
        submissionEntity.setUser(userEntity);
        submissionEntity.setSubmissionCode(submissionCode);
        submissionEntity.setFailedOnTestCase(failedTestCase);

        submissionEntity.setSubmissionStatus(responseDto.getStatus());
        submissionEntity.setCodeOutput(
                responseDto.getCodeOutput() != null ? responseDto.getCodeOutput().trim() : ""
        );
        submissionEntity.setUserLogs(
                responseDto.getUserLogs() != null ? responseDto.getUserLogs() : ""
        );

        boolean accepted = responseDto.getStatus() == SubmissionStatus.ACC;

        SubmissionEvent event = new SubmissionEvent(problemEntity.getId(), accepted);
        SubmissionUserEvent userEvent = new SubmissionUserEvent(userEntity.getId(), problemEntity.getId());

        // Only publish a SolvedProblemEvent if the problem was actually solved
        if (accepted) {
            SolvedProblemEvent solvedProblemEvent = new SolvedProblemEvent(problemEntity, userEntity);
            eventPublisher.publishEvent(solvedProblemEvent);
        }

        eventPublisher.publishEvent(userEvent);
        eventPublisher.publishEvent(event);

        repository.save(submissionEntity);
    }


    public RunCodeResponseDto runCustomCode(RunCodeRequestDto requestDto) {
        ProblemEntity problem = problemService.findProblemByIdGetEntity(requestDto.getProblemId())
                .orElseThrow(() -> new ProblemNotFoundException("Invalid problem id"));

        String finalSourceCode = buildFinalSourceCode(problem, requestDto.getLanguageId(), requestDto.getSourceCode());

        StringBuilder sb = new StringBuilder();
        sb.append(requestDto.getCustomTestCases().size()).append("\n");
        for (String tc : requestDto.getCustomTestCases()) {
            sb.append(tc.trim()).append("\n");
        }
        String combinedInput = sb.toString();

        Judge0RequestDto batchRequest = new Judge0RequestDto(
                finalSourceCode,
                requestDto.getLanguageId(),
                combinedInput,
                null,
                (int)(problem.getTimeLimitInMS() / 1000),
                (int)(problem.getMemoryLimitInMB() * 1024),
                20.0
        );

        List<String> tokens = judge0Service.submitBatch(List.of(batchRequest));
        Judge0ResponseDto response = judge0Service.getBatchResults(tokens).get(0);

        return evaluateCustomRun(problem, requestDto.getCustomTestCases(), response);
    }

    private RunCodeResponseDto evaluateCustomRun(ProblemEntity problem, List<String> customInputs, Judge0ResponseDto response) {
        RunCodeResponseDto result = new RunCodeResponseDto();
        SubmissionStatus globalStatus = response.getMappedStatus();
        result.setGlobalStatus(globalStatus);

        if (globalStatus == SubmissionStatus.CE) {
            result.setCompileError(response.compileOutput());
            return result;
        }

        String rawOutput = response.stdout() != null ? response.stdout() : "";
        List<CustomTestCaseResultDto> testCaseResults = new java.util.ArrayList<>();

        for (String input : customInputs) {
            CustomTestCaseResultDto tcResult = new CustomTestCaseResultDto();
            tcResult.setInput(input);

            String beginTag = "~CASE_BEGIN~";
            String endTag = "~CASE_END~";
            int beginIdx = rawOutput.indexOf(beginTag);
            int endIdx = rawOutput.indexOf(endTag);

            if (beginIdx == -1 || endIdx == -1 || beginIdx > endIdx) {
                tcResult.setStatus(globalStatus == SubmissionStatus.TLE ? SubmissionStatus.TLE : SubmissionStatus.RTE);
                tcResult.setUserOutput(response.stderr() != null ? response.stderr().trim() : "Process crashed or timed out.");
                testCaseResults.add(tcResult);
                continue;
            }

            String caseBlock = rawOutput.substring(beginIdx + beginTag.length(), endIdx).trim();
            rawOutput = rawOutput.substring(endIdx + endTag.length());

            int logsTagStart = caseBlock.indexOf("~USER_LOGS~");
            int funcOutTagStart = caseBlock.indexOf("~FUNC_OUT~");
            int timeTagStart = caseBlock.indexOf("~TIME|");

            if (logsTagStart == -1 || funcOutTagStart == -1 || timeTagStart == -1) {
                tcResult.setStatus(SubmissionStatus.RTE);
                tcResult.setUserOutput("Malformed output from execution.");
                testCaseResults.add(tcResult);
                continue;
            }

            int timeTagEnd = caseBlock.indexOf("~", timeTagStart + 6);
            if (timeTagEnd == -1) {
                tcResult.setStatus(SubmissionStatus.RTE);
                testCaseResults.add(tcResult);
                continue;
            }

            tcResult.setUserOutput(caseBlock.substring(logsTagStart + 11, funcOutTagStart).trim());
            tcResult.setCodeOutput(caseBlock.substring(funcOutTagStart + 10, timeTagStart).trim());

            try {
                double executionTimeMs = Double.parseDouble(caseBlock.substring(timeTagStart + 6, timeTagEnd).trim());
                tcResult.setExecutionTimeMs(executionTimeMs);
                if (executionTimeMs > problem.getTimeLimitInMS()) {
                    tcResult.setStatus(SubmissionStatus.TLE);
                } else {
                    tcResult.setStatus(SubmissionStatus.ACC);
                }
            } catch (NumberFormatException e) {
                tcResult.setStatus(SubmissionStatus.RTE);
            }

            testCaseResults.add(tcResult);
        }

        result.setResults(testCaseResults);
        return result;
    }

    public List<RecentSubmissionDto> getRecentSubmissions(int limit, SubmissionStatus status) {
        Long userId = userService.getCurrentUserDetailsInEntity().getId();

        // Spring Data Pageable to enforce the limit
        Pageable pageable = PageRequest.of(0, limit);

        List<SubmissionEntity> submissions = repository.findRecentSubmissionsUsingStatusWithProblemData(userId, status, pageable);

        return submissions.stream().map(sub -> new RecentSubmissionDto(
                sub.getProblem().getProblemTitle(),
                sub.getSubmissionStatus(),
                sub.getLanguageId(),
                sub.getSubmissionDateTime()
        )).toList();
    }

    private record EvaluationResult(SubmissionStatus status, int passedCount, TestCaseEntity failedTestCase, Judge0ResponseDto failedResponse, String actualOutput, String userLogs) {}
}