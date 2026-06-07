package com.example.ByteForge.problems.stats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemStatsResponseDto {
    private Long id;
    private Long problemId;
    private Long totalSubmissions;
    private Long totalAccepted;
    private Long totalLikes;
    private Long totalComments;
    private Long totalSolutionsAvailable;
    private Long totalEditorialsAvailable;
    private Long totalStars;
}