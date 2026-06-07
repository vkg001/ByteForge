package com.example.ByteForge.problems.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseRequestDto {
    private String input;
    private String output;
    private Boolean hidden;
    private Boolean hiddenAfterFailure;
}