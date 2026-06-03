package com.example.ByteForge.submissions.entities;

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
public class SubmissionDto {
    private List<SubmissionEntity> allSubmissions = new ArrayList<>();
    private ProblemSubmissionStatus status;
}
