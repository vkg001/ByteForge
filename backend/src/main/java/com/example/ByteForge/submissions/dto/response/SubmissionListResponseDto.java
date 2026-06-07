package com.example.ByteForge.submissions.dto.response;

import com.example.ByteForge.submissions.enums.ProblemSubmissionStatus;
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
public class SubmissionListResponseDto {
    private List<SubmissionResponseDto> allSubmissions = new ArrayList<>();
    private ProblemSubmissionStatus status;
}
