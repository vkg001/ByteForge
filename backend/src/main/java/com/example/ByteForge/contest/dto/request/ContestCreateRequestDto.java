package com.example.ByteForge.contest.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContestCreateRequestDto {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}