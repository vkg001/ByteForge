package com.example.ByteForge.contest.controller;

import com.example.ByteForge.config.rate_limit.RateLimit;
import com.example.ByteForge.contest.dto.message.RawSubmissionMessage;
import com.example.ByteForge.contest.dto.response.ContestProblemResponseDto;
import com.example.ByteForge.contest.service.ContestService;
import com.example.ByteForge.contest.service.ContestSubmissionProducer;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user/contest")
@RequiredArgsConstructor
public class ContestControllerUser {

    private final ContestService contestService;
    private final ContestSubmissionProducer submissionProducer;
    private final UserService userService;

    @PostMapping("/{contestId}/register")
    public ResponseEntity<Void> registerForContest(@PathVariable Long contestId) {
        contestService.registerUser(contestId);
        return ResponseEntity.ok().build();
    }

    @RateLimit(key = "problem-submission-from-contest-controller-user", capacity = 1, refillTokens = 1, refillDurationInSeconds = 5)
    @PostMapping("/{contestId}/submit")
    public ResponseEntity<String> submitCode(@PathVariable Long contestId, @RequestBody RawSubmissionMessage request) {
        UserEntity user = userService.getCurrentUserDetailsInEntity();

        request.setContestId(contestId);
        request.setUserId(user.getId());
        request.setUsername(user.getName());
        request.setSubmissionTime(LocalDateTime.now());

        submissionProducer.sendRawSubmission(request);

        return ResponseEntity.accepted().body("Processing");
    }

    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<Object> getLeaderboard(
            @PathVariable Long contestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(contestService.getLeaderboard(contestId, page, size));
    }

    @GetMapping("/{contestId}/problems")
    public ResponseEntity<List<ContestProblemResponseDto>> getContestProblems(@PathVariable Long contestId) {
        return ResponseEntity.ok(contestService.getContestProblems(contestId));
    }
}