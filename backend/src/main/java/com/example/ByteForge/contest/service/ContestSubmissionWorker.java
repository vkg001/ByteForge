package com.example.ByteForge.contest.service;

import com.example.ByteForge.contest.dto.message.ContestSubmissionMessage;
import com.example.ByteForge.contest.dto.message.RawSubmissionMessage;
import com.example.ByteForge.contest.repository.SubmissionBatchRepository;
import com.example.ByteForge.judge0.Judge0Service;
import com.example.ByteForge.judge0.dto.request.Judge0RequestDto;
import com.example.ByteForge.judge0.dto.response.Judge0ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestSubmissionWorker {

    private final Judge0Service judge0Service;
    private final RedisLeaderboardService redisLeaderboardService;
    private final SubmissionBatchRepository batchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topicPattern = "contest-.*-submissions-raw", containerFactory = "batchFactory", properties = {
            "max.poll.records=50",
            "fetch.max.wait.ms=1000"
    })
    public void processSubmissions(List<String> rawMessages) {
        List<RawSubmissionMessage> messages = rawMessages.stream()
                .map(this::parseMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (messages.isEmpty()) return;

        List<Judge0RequestDto> judgeRequests = messages.stream()
                .map(this::buildJudgeRequest)
                .collect(Collectors.toList());

        List<String> tokens = judge0Service.submitBatch(judgeRequests);

        waitForJudge0Execution();

        List<Judge0ResponseDto> judgeResponses = judge0Service.getBatchResults(tokens);

        List<ContestSubmissionMessage> evaluatedSubmissions = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            RawSubmissionMessage raw = messages.get(i);
            Judge0ResponseDto response = judgeResponses.get(i);

            String status = response.getMappedStatus().name();
            boolean isAccepted = "ACC".equals(status);

            if (isAccepted) {
                redisLeaderboardService.updateScore(
                        raw.getContestId(),
                        raw.getUserId(),
                        raw.getUsername(),
                        100, // Make dynamic based on problem score logic
                        calculatePenalty(raw)
                );
            }

            evaluatedSubmissions.add(buildFinalSubmissionMessage(raw, response, status));
        }

        batchRepository.batchInsertSubmissions(evaluatedSubmissions);
    }

    private RawSubmissionMessage parseMessage(String msg) {
        try {
            return objectMapper.readValue(msg, RawSubmissionMessage.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Judge0RequestDto buildJudgeRequest(RawSubmissionMessage msg) {
        return new Judge0RequestDto(
                msg.getSubmissionCode(),
                msg.getLanguageId(),
                "dummy_input", // Replace with real input fetching logic
                "dummy_output",
                2,
                128000,
                20.0
        );
    }

    private ContestSubmissionMessage buildFinalSubmissionMessage(RawSubmissionMessage raw, Judge0ResponseDto response, String status) {
        ContestSubmissionMessage finalMsg = new ContestSubmissionMessage();
        finalMsg.setContestId(raw.getContestId());
        finalMsg.setProblemId(raw.getProblemId());
        finalMsg.setUserId(raw.getUserId());
        finalMsg.setLanguageId(raw.getLanguageId());
        finalMsg.setSubmissionCode(raw.getSubmissionCode());
        finalMsg.setStatus(status);
        finalMsg.setCodeOutput(response.stdout());
        finalMsg.setUserLogs(response.stderr());
        finalMsg.setSubmissionTime(raw.getSubmissionTime());
        return finalMsg;
    }

    private long calculatePenalty(RawSubmissionMessage msg) {
        return 0L; // Implement your penalty calculation logic here
    }

    private void waitForJudge0Execution() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}