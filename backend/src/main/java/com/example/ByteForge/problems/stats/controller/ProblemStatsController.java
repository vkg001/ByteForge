package com.example.ByteForge.problems.stats.controller;

import com.example.ByteForge.problems.stats.dto.response.ProblemStatsResponseDto;
import com.example.ByteForge.problems.stats.service.ProblemStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems/stats")
@RequiredArgsConstructor
public class ProblemStatsController {

    private final ProblemStatsService problemStatsService;

    @GetMapping("/{problemId}/stats")
    public ResponseEntity<ProblemStatsResponseDto> getProblemStats(@PathVariable("problemId") Long problemId) {
        return problemStatsService.getStatsByProblemId(problemId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}