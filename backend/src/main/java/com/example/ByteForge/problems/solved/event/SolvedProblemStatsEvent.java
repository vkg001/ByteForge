package com.example.ByteForge.problems.solved.event;

import com.example.ByteForge.problems.core.enums.ProblemDifficulty;
import com.example.ByteForge.user.core.entity.UserEntity;

public record SolvedProblemStatsEvent(UserEntity userEntity, ProblemDifficulty problemDifficulty) {
}
