package com.example.ByteForge.contest.dto.message;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContestSubmissionMessage {
    private Long contestId;
    private Long problemId;
    private Long userId;
    private Integer languageId;
    private String submissionCode;
    private String status;
    private String codeOutput;
    private String userLogs;
    private LocalDateTime submissionTime;
}