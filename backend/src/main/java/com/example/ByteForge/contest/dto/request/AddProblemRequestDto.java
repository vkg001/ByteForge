package com.example.ByteForge.contest.dto.request;

import lombok.Data;

@Data
public class AddProblemRequestDto {
    private Long problemId;
    private Integer score;
}