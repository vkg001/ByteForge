package com.example.ByteForge.problems.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "testcases")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private ProblemEntity problem;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String output;

    @Column(nullable = false)
    private Boolean hidden;

    @Column(nullable = false)
    private Boolean hiddenAfterFailure;
}