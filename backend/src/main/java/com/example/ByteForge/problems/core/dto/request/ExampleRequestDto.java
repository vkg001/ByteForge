package com.example.ByteForge.problems.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExampleRequestDto {
    private String input;
    private String output;
    private String explanation;
}
