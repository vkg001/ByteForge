package com.example.ByteForge.problems.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoilerPlateCode {
    @Column(nullable = false)
    private int languageCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prependCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String appendCode;
}
