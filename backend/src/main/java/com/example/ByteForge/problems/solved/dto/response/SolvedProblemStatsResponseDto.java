package com.example.ByteForge.problems.solved.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolvedProblemStatsResponseDto {
    private Long id;
    private Long userStatsId;
    private Long school;
    private Long easy;
    private Long medium;
    private Long hard;
    private Long extreme;
}