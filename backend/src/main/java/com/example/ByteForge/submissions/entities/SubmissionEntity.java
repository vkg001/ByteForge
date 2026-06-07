package com.example.ByteForge.submissions.entities;

import com.example.ByteForge.user.entities.UserEntity;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.entities.TestCaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "problem_id")
    private ProblemEntity problem;

    @ManyToOne
    @JoinColumn(nullable = false, name = "register_id")
    private UserEntity user;

    @Column(nullable = false, updatable = false)
    private int languageId;

    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String submissionCode; // code submitted by user

    @ManyToOne
    @JoinColumn(nullable = true, name = "testcase_id")
    private TestCaseEntity failedOnTestCase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private SubmissionStatus submissionStatus;

    @Column(nullable = true, updatable = false, columnDefinition = "TEXT")
    private String codeOutput;

    @Column(nullable = true, updatable = false, columnDefinition = "TEXT")
    private String userLogs;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime submissionDateTime;
}
