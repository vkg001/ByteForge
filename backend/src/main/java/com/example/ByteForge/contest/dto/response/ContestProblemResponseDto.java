package com.example.ByteForge.contest.dto.response;

import com.example.ByteForge.problems.core.dto.response.ProblemResponseDto;
import lombok.Data;

@Data
public class ContestProblemResponseDto {
    private Long contestProblemId;
    private Integer score;
    private ProblemResponseDto problem;
}