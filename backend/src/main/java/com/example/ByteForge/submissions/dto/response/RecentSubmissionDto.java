package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.submissions.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentSubmissionDto {
    private String problemTitle;
    private SubmissionStatus status;
    private int languageId;
    private LocalDateTime submittedAt;
}