package com.example.ByteForge.submissions;

import com.example.ByteForge.auth.signup.UserEntity;
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
    private String submissionCode;

    @Column(nullable = false, updatable = false)
    private Boolean accepted;

    @Column(nullable = true, updatable = false)
    private TestCaseEntity failedOnTestCase;

    @Column(nullable = true, updatable = false)
    private String codeOutput;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime submissionDateTime;
}
