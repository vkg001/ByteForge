package com.example.ByteForge.problems.solved.event;

import com.example.ByteForge.problems.core.entities.ProblemEntity;
import com.example.ByteForge.user.core.entity.UserEntity;

public record SolvedProblemEvent(ProblemEntity problemEntity, UserEntity userEntity) {
}
