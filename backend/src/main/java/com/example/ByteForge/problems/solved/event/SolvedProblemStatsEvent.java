package com.example.ByteForge.problems.solved.event;

import com.example.ByteForge.problems.core.enums.ProblemDifficulty;

public record SolvedProblemStatsEvent(Long userId, ProblemDifficulty problemDifficulty) {
}