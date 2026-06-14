package com.example.ByteForge.contest.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContestResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}