package com.example.ByteForge.submissions.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitCodeRequestDto {
    private int languageId;
    private Long problemId;
    private String sourceCode;
}
