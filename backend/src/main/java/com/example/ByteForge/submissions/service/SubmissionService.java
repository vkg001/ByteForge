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
import java.util.Optional;
import java.util.stream.Collectors;

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
        Long problemId = requestDto.getProblemId();
        int languageId = requestDto.getLanguageId();
        String sourceCode = requestDto.getSourceCode();

        ProblemEntity problem = problemService.findProblemByIdGetEntity(problemId)
                .orElseThrow(() -> new ProblemNotFoundException("Invalid problem id"));

        String finalSourceCode = buildFinalSourceCode(problem, languageId, sourceCode);
        List<Judge0ResponseDto> results = executeAgainstJudge0(problem, finalSourceCode, languageId);

        EvaluationResult evaluation = evaluateResults(problem.getTestCases(), results);
        saveSubmission(problem, languageId, sourceCode, evaluation.failedTestCase(), evaluation.failedResponse());

        return buildResponseDto(evaluation, problem.getTestCases().size());
    }

    private String buildFinalSourceCode(ProblemEntity problem, int languageId, String sourceCode) {
        return problem.getBoilerPlateCodes().stream()
                .filter(bp -> bp.getLanguageCode() == languageId)
                .findFirst()
                .map(bp -> bp.getPrependCode() + sourceCode + bp.getAppendCode())
                .orElseThrow(() -> new ProblemNotFoundException("Invalid language code"));
    }

    private List<Judge0ResponseDto> executeAgainstJudge0(ProblemEntity problem, String finalSourceCode, int languageId) {
        List<Judge0RequestDto> batchRequests = problem.getTestCases().stream().map(testCase ->
                new Judge0RequestDto(
                        finalSourceCode, languageId, testCase.getInput(), testCase.getOutput(),
                        problem.getTimeLimitInMS() / 1000.0, (int) (problem.getMemoryLimitInMB() * 1024), 5.0
                )
        ).collect(Collectors.toList());

        List<String> tokens = judge0Service.submitBatch(batchRequests);
        return judge0Service.getBatchResults(tokens);
    }

    private EvaluationResult evaluateResults(List<TestCaseEntity> testCases, List<Judge0ResponseDto> results) {
        int passed = 0;

        for (int i = 0; i < results.size(); i++) {
            Judge0ResponseDto response = results.get(i);
            TestCaseEntity testCase = testCases.get(i);
            SubmissionStatus status = response.getMappedStatus();

            if (status != SubmissionStatus.ACC) {
                return new EvaluationResult(status, passed, testCase, response);
            }
            passed++;
        }

        Judge0ResponseDto lastResponse = results.isEmpty() ? null : results.get(results.size() - 1);
        return new EvaluationResult(SubmissionStatus.ACC, passed, null, lastResponse);
    }

    private SubmitCodeResponseDto buildResponseDto(EvaluationResult eval, int totalTestCases) {
        SubmitCodeResponseDto dto = new SubmitCodeResponseDto();
        dto.setStatus(eval.status());
        dto.setTotalPassed(eval.passedCount());
        dto.setTotalTestCases(totalTestCases);

        Judge0ResponseDto response = eval.failedResponse();
        if (response != null) {
            dto.setCodeOutput(response.stdout() != null ? response.stdout().trim() : "");

            if (eval.status() == SubmissionStatus.CE) {
                dto.setError(response.compileOutput());
                return dto;
            }
            if (eval.status() == SubmissionStatus.RTE) {
                dto.setError(response.stderr());
                return dto;
            }

            dto.setUserLogs(response.stderr());

            if (eval.status() == SubmissionStatus.WA) dto.setError("Wrong Answer");
            if (eval.status() == SubmissionStatus.TLE) dto.setError("Time Limit Exceeded");
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

        Boolean accepted = executionResponse == null  ||  executionResponse.getMappedStatus() == SubmissionStatus.ACC;
        SubmissionEvent event = new SubmissionEvent(problemEntity.getId(), accepted);
        eventPublisher.publishEvent(event);
        repository.save(submissionEntity);
    }

    private record EvaluationResult(SubmissionStatus status, int passedCount, TestCaseEntity failedTestCase, Judge0ResponseDto failedResponse) {}
}