package com.example.ByteForge.problems.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoilerplateCodeUpdateRequestDto {
    private int languageCode;
    private Long problemId;
    private String userCode;
    private String prependCode;
    private String appendCode;
}
