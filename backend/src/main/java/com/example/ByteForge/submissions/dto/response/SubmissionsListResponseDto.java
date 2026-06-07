package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.submissions.entities.ProblemSubmissionStatus;
import com.example.ByteForge.submissions.entities.SubmissionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionsListResponseDto {
    private List<SubmissionEntity> allSubmissions = new ArrayList<>();
    private ProblemSubmissionStatus status;
}
