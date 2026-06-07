package com.example.ByteForge.problems.entities;

import com.example.ByteForge.problems.dto.request.BoilerPlateCodeRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoilerPlateCodeEntity {
    @Column(nullable = false)
    private int languageCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prependCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String appendCode;
}
