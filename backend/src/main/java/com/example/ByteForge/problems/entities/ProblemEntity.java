package com.example.ByteForge.problems.entities;

import com.example.ByteForge.problems.ProblemDifficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Table(name = "problems")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProblemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 50, message = "Problem title should be of 3 to 50 characters.")
    @Column(nullable = false, unique = true)
    private String problemTitle;

    @Column(nullable = false)
    @Size(min = 10, max = 2000, message = "Problem Statement should be between 10 to 2000 characters")
    private String problemStatement;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> constraints = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemDifficulty problemDifficulty;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> hints = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> companyTags;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "problem_examples", joinColumns = @JoinColumn(name = "problem_id"))
    private List<ExampleEntity> examples = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> topics = new ArrayList<>();
}
