package com.example.ByteForge.problems.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoilerplateCodeResponseDto {
    private int languageCode;
    private String userCode;
    private String prependCode;
    private String appendCode;
}
