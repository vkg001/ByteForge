package com.example.ByteForge.contest.dto.response;

import lombok.Data;

@Data
public class ContestLeaderboardResponseDto {
    private Long userId;
    private String username;
    private Integer score;
    private Long penaltyTimeInSeconds;
}