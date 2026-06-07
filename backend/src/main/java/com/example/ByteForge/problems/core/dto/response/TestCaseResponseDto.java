package com.example.ByteForge.problems.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResponseDto {
    private String input;
    private String output;
    private Boolean hidden;
    private Boolean hiddenAfterFailure;
}