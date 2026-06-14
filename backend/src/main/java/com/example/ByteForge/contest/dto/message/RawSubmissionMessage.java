package com.example.ByteForge.contest.dto.message;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RawSubmissionMessage {
    private Long contestId;
    private Long problemId;
    private Long userId;
    private String username;
    private Integer languageId;
    private String submissionCode;
    private LocalDateTime submissionTime;
}