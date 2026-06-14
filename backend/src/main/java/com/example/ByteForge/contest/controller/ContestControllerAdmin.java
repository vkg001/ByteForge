package com.example.ByteForge.contest.controller;

import com.example.ByteForge.contest.dto.request.AddProblemRequestDto;
import com.example.ByteForge.contest.dto.request.ContestCreateRequestDto;
import com.example.ByteForge.contest.dto.response.ContestResponseDto;
import com.example.ByteForge.contest.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/contest")
@RequiredArgsConstructor
public class ContestControllerAdmin {

    private final ContestService contestService;

    @PostMapping
    public ResponseEntity<ContestResponseDto> createContest(@RequestBody ContestCreateRequestDto request) {
        return ResponseEntity.ok(contestService.createContest(request));
    }

    @PostMapping("/{contestId}/problems")
    public ResponseEntity<Void> addProblem(@PathVariable Long contestId, @RequestBody AddProblemRequestDto request) {
        contestService.addProblemToContest(contestId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contestId}/finalize")
    public ResponseEntity<Void> finalizeLeaderboard(@PathVariable Long contestId) {
        contestService.finalizeContestLeaderboard(contestId);
        return ResponseEntity.ok().build();
    }
}