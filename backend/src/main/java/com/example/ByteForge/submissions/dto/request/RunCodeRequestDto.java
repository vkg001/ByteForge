package com.example.ByteForge.submissions.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class RunCodeRequestDto {
    @NotNull
    private Long problemId;

    @NotNull
    private Integer languageId;

    @NotBlank
    private String sourceCode;

    @NotNull
    @Size(max = 10)
    private List<String> customTestCases;
}