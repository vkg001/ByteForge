package com.example.ByteForge.problems.core.entities;

import com.example.ByteForge.problems.core.enums.ProblemDifficulty;
import com.example.ByteForge.problems.core.enums.ProblemVisibility;
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

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCaseEntity> testCases = new ArrayList<>();

    @Column(nullable = false)
    private Long memoryLimitInMB;

    @Column(nullable = false)
    private Long timeLimitInMS;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<Long> similarQuestions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "boilerplate_codes", joinColumns = @JoinColumn(name = "problem_id"))
    private List<BoilerplateCodeEntity> boilerPlateCodes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = true)
    private ProblemVisibility problemVisibility;

    public void setTestCases(List<TestCaseEntity> testCases) {
        if (this.testCases == null) {
            this.testCases = new ArrayList<>();
        } else {
            this.testCases.clear();
        }

        if (testCases != null) {
            for (TestCaseEntity testCase : testCases) {
                addTestCase(testCase);
            }
        }
    }

    public void addTestCase(TestCaseEntity testCase) {
        testCases.add(testCase);
        testCase.setProblem(this);
    }
}