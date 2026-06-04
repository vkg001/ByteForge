package com.example.ByteForge.submissions.entities;

import com.example.ByteForge.user.UserEntity;
import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.entities.TestCaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "problem_id")
    private ProblemEntity problem;

    @ManyToOne
    @JoinColumn(nullable = false, name = "register_id")
    private UserEntity user;

    @Column(nullable = false, updatable = false)
    private String submissionCode; // code submitted by user

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private TestCaseEntity failedOnTestCase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private SubmissionStatus submissionStatus;

    @Column(nullable = true, updatable = false)
    private String codeOutput;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime submissionDateTime;
}
