package com.example.ByteForge.user.stats.dto.response;

import com.example.ByteForge.problems.solved.dto.response.SolvedProblemStatsResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponseDto {
    private Long id;
    private Long userId;
    private Long totalSubmissions;

    // The newly nested DTO to prevent N+1 API calls from the frontend
    private SolvedProblemStatsResponseDto problemStats;

    private LocalDateTime lastSubmissionDate;
    private LocalDateTime lastActivityDate;
    private Long reputation;
    private Long totalComments;
    private Long totalSolutionsAdded;
    private Integer maxStreak;
    private Integer currentStreak;
}